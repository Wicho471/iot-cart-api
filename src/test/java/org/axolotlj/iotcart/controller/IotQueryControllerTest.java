package org.axolotlj.iotcart.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.axolotlj.iotcart.dto.response.UltimasSecuenciasDto;
import org.axolotlj.iotcart.dto.response.UltimosMovimientosDto;
import org.axolotlj.iotcart.dto.response.UltimosObstaculosDto;
import org.axolotlj.iotcart.service.IotEventService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Pruebas unitarias para IotQueryController.
 * Actualizadas para esperar el formato ApiResponse { success, data, error }.
 */
@WebMvcTest(IotQueryController.class)
public class IotQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IotEventService iotEventService;

    // ... (testGetUltimosMovimientos y testGetUltimosObstaculos sin cambios) ...
    @Test
    public void testGetUltimosMovimientos() throws Exception {
        // Arrange
        String dispositivo = "ROVER-CURIOSITY";
        int limite = 5;

        UltimosMovimientosDto mockMovimiento = Mockito.mock(UltimosMovimientosDto.class);
        when(mockMovimiento.getFecha_evento()).thenReturn(Timestamp.from(Instant.now()));
        when(mockMovimiento.getNombre_dispositivo()).thenReturn(dispositivo);
        when(mockMovimiento.getMovimiento()).thenReturn("Adelante");
        when(mockMovimiento.getIp_cliente()).thenReturn("192.168.1.100");

        List<UltimosMovimientosDto> mockList = Collections.singletonList(mockMovimiento);

        when(iotEventService.obtenerUltimosMovimientos(dispositivo, limite))
                .thenReturn(mockList);

        // Act & Assert
        mockMvc.perform(get("/api/v1/iot/query/movimiento/{dispositivoNombre}", dispositivo)
                .param("limite", String.valueOf(limite))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true))) // Verificar nueva estructura
                .andExpect(jsonPath("$.data", hasSize(1))) // Verificar array anidado
                .andExpect(jsonPath("$.data[0].nombre_dispositivo", is(dispositivo)))
                .andExpect(jsonPath("$.data[0].movimiento", is("Adelante")));
    }

    @Test
    public void testGetUltimosObstaculos() throws Exception {
        // Arrange
        String dispositivo = "DRON-INGENUITY";
        int limite = 2;

        UltimosObstaculosDto mockObstaculo = Mockito.mock(UltimosObstaculosDto.class);
        when(mockObstaculo.getFecha_evento()).thenReturn(Timestamp.from(Instant.now()));
        when(mockObstaculo.getNombre_dispositivo()).thenReturn(dispositivo);
        when(mockObstaculo.getObstaculo_detectado()).thenReturn("Adelante-Derecha");
        when(mockObstaculo.getIp_cliente()).thenReturn("200.1.1.1");

        List<UltimosObstaculosDto> mockList = Collections.singletonList(mockObstaculo);

        when(iotEventService.obtenerUltimosObstaculos(dispositivo, limite))
                .thenReturn(mockList);

        // Act & Assert
        mockMvc.perform(get("/api/v1/iot/query/obstaculo/{dispositivoNombre}", dispositivo)
                .param("limite", String.valueOf(limite))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true))) // Verificar nueva estructura
                .andExpect(jsonPath("$.data", hasSize(1))) // Verificar array anidado
                .andExpect(jsonPath("$.data[0].nombre_dispositivo", is(dispositivo)))
                .andExpect(jsonPath("$.data[0].obstaculo_detectado", is("Adelante-Derecha")));
    }


    /**
     * Prueba el endpoint GET /api/v1/iot/query/secuencia
     * ACTUALIZADO: Verifica que 'pasos' sea un arreglo.
     */
    @Test
    public void testGetUltimasSecuenciasDemo() throws Exception {
        // Arrange
        UltimasSecuenciasDto mockSecuencia = Mockito.mock(UltimasSecuenciasDto.class);
        when(mockSecuencia.getId_secuencia()).thenReturn(1);
        when(mockSecuencia.getNombre_secuencia()).thenReturn("Secuencia de prueba");
        when(mockSecuencia.getFecha_creacion()).thenReturn(Timestamp.from(Instant.now()));
        when(mockSecuencia.getNumero_pasos()).thenReturn(3L);
        // El servicio/repo sigue devolviendo un STRING
        when(mockSecuencia.getPasos()).thenReturn("Adelante -> Giro 90° derecha -> Detener");

        List<UltimasSecuenciasDto> mockList = Collections.singletonList(mockSecuencia);

        when(iotEventService.obtenerUltimasSecuenciasDemo()).thenReturn(mockList);

        // Act & Assert
        mockMvc.perform(get("/api/v1/iot/query/secuencia")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id_secuencia", is(1)))
                .andExpect(jsonPath("$.data[0].nombre_secuencia", is("Secuencia de prueba")))
                .andExpect(jsonPath("$.data[0].numero_pasos", is(3)))
                // --- INICIO DE CORRECCIÓN ---
                // Validar que 'pasos' es un arreglo
                .andExpect(jsonPath("$.data[0].pasos", hasSize(3)))
                .andExpect(jsonPath("$.data[0].pasos[0]", is("Adelante")))
                .andExpect(jsonPath("$.data[0].pasos[1]", is("Giro 90° derecha")))
                .andExpect(jsonPath("$.data[0].pasos[2]", is("Detener")));
                // --- FIN DE CORRECCIÓN ---
    }

    @Test
    public void testGetUltimosMovimientos_DefaultLimit() throws Exception {
        // Arrange
        String dispositivo = "ROVER-CURIOSITY";
        int defaultLimite = 10; 

        List<UltimosMovimientosDto> mockList = Collections.emptyList();

        when(iotEventService.obtenerUltimosMovimientos(dispositivo, defaultLimite))
                .thenReturn(mockList);

        // Act & Assert
        mockMvc.perform(get("/api/v1/iot/query/movimiento/{dispositivoNombre}", dispositivo)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true))) // Verificar nueva estructura
                .andExpect(jsonPath("$.data", hasSize(0))); // Verificar array anidado vacío
    }
}