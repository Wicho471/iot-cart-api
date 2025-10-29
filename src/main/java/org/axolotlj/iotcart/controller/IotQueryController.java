package org.axolotlj.iotcart.controller;

import org.axolotlj.iotcart.dto.response.UltimasSecuenciasDto;
import org.axolotlj.iotcart.dto.response.UltimosMovimientosDto;
import org.axolotlj.iotcart.dto.response.UltimosObstaculosDto;
import org.axolotlj.iotcart.service.IotEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para las operaciones de consulta (lectura) del dispositivo IoT.
 * Corresponde a los SPs que obtienen datos.
 */
@RestController
@RequestMapping("/api/v1/iot/query")
public class IotQueryController {

    private final IotEventService iotEventService;

    /**
     * Inyecta el servicio de eventos.
     */
    @Autowired
    public IotQueryController(IotEventService iotEventService) {
        this.iotEventService = iotEventService;
    }

    /**
     * Endpoint para obtener los últimos movimientos (SP b, c).
     */
    @GetMapping("/movimiento/{dispositivoNombre}")
    public ResponseEntity<List<UltimosMovimientosDto>> getUltimosMovimientos(
            @PathVariable String dispositivoNombre,
            @RequestParam(defaultValue = "10") int limite) {
        
        List<UltimosMovimientosDto> movimientos = iotEventService.obtenerUltimosMovimientos(dispositivoNombre, limite);
        return ResponseEntity.ok(movimientos);
    }

    /**
     * Endpoint para obtener los últimos obstáculos (SP h, i).
     */
    @GetMapping("/obstaculo/{dispositivoNombre}")
    public ResponseEntity<List<UltimosObstaculosDto>> getUltimosObstaculos(
            @PathVariable String dispositivoNombre,
            @RequestParam(defaultValue = "10") int limite) {
        
        List<UltimosObstaculosDto> obstaculos = iotEventService.obtenerUltimosObstaculos(dispositivoNombre, limite);
        return ResponseEntity.ok(obstaculos);
    }

    /**
     * Endpoint para obtener las últimas secuencias DEMO (SP e).
     */
    @GetMapping("/secuencia")
    public ResponseEntity<List<UltimasSecuenciasDto>> getUltimasSecuenciasDemo() {
        List<UltimasSecuenciasDto> secuencias = iotEventService.obtenerUltimasSecuenciasDemo();
        return ResponseEntity.ok(secuencias);
    }
}