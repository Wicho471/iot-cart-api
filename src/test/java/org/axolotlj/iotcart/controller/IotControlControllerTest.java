package org.axolotlj.iotcart.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.axolotlj.iotcart.dto.request.EjecutarSecuenciaRequest;
import org.axolotlj.iotcart.dto.request.MovimientoRequest;
import org.axolotlj.iotcart.dto.request.ObstaculoRequest;
import org.axolotlj.iotcart.dto.request.SecuenciaDemoRequest;
import org.axolotlj.iotcart.service.IotEventService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas unitarias para IotControlController.
 * Utiliza @WebMvcTest para probar solo la capa web, mockeando el servicio.
 */
@WebMvcTest(IotControlController.class)
public class IotControlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // Para convertir DTOs a JSON

    @MockBean
    private IotEventService iotEventService;

    /**
     * Prueba el endpoint POST /api/v1/iot/control/movimiento
     */
    @Test
    public void testRegistrarMovimiento() throws Exception {
        // Arrange
        MovimientoRequest request = new MovimientoRequest();
        request.setNombreDispositivo("ROVER-TEST");
        request.setCodigoOperacion("ADELANTE");
        request.setIpCliente("127.0.0.1");
        
        Long expectedEventId = 123L;
        
        // Definir el comportamiento del mock
        when(iotEventService.registrarMovimiento(any(MovimientoRequest.class)))
                .thenReturn(expectedEventId);

        // Act & Assert
        mockMvc.perform(post("/api/v1/iot/control/movimiento")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated()) // Espera HTTP 201
                .andExpect(jsonPath("$.id").value(expectedEventId)); // Verifica el ID en el JSON de respuesta
    }

    /**
     * Prueba el endpoint POST /api/v1/iot/control/obstaculo
     */
    @Test
    public void testRegistrarObstaculo() throws Exception {
        // Arrange
        ObstaculoRequest request = new ObstaculoRequest();
        request.setNombreDispositivo("ROVER-TEST");
        request.setCodigoObstaculo("OBS_ADELANTE");
        request.setIpCliente("127.0.0.1");

        Long expectedEventId = 456L;

        when(iotEventService.registrarObstaculo(any(ObstaculoRequest.class)))
                .thenReturn(expectedEventId);

        // Act & Assert
        mockMvc.perform(post("/api/v1/iot/control/obstaculo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(expectedEventId));
    }

    /**
     * Prueba el endpoint POST /api/v1/iot/control/secuencia
     */
    @Test
    public void testCrearSecuenciaDemo() throws Exception {
        // Arrange
        SecuenciaDemoRequest request = new SecuenciaDemoRequest();
        request.setNombreSecuencia("Prueba de Secuencia");
        request.setCantidadMovimientos(5);

        Integer expectedSequenceId = 789;

        when(iotEventService.crearSecuenciaDemo(any(SecuenciaDemoRequest.class)))
                .thenReturn(expectedSequenceId);

        // Act & Assert
        mockMvc.perform(post("/api/v1/iot/control/secuencia")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(expectedSequenceId));
    }

    /**
     * Prueba el endpoint POST /api/v1/iot/control/secuencia/ejecutar
     */
    @Test
    public void testEjecutarSecuenciaDemo() throws Exception {
        // Arrange
        EjecutarSecuenciaRequest request = new EjecutarSecuenciaRequest();
        request.setIdSecuencia(1);
        request.setNombreDispositivo("ROVER-TEST");
        request.setIpCliente("127.0.0.1");
        request.setPais("México");
        request.setCiudad("CDMX");
        request.setLatitud(BigDecimal.valueOf(19.4326));
        request.setLongitud(BigDecimal.valueOf(-99.1332));
        
        // El método de servicio es void, así que usamos doNothing
        doNothing().when(iotEventService).ejecutarSecuenciaDemo(any(EjecutarSecuenciaRequest.class));

        // Act & Assert
        mockMvc.perform(post("/api/v1/iot/control/secuencia/ejecutar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()); // Espera HTTP 200
    }
}