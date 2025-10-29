package org.axolotlj.iotcart.controller;

import org.axolotlj.iotcart.dto.request.EjecutarSecuenciaRequest;
import org.axolotlj.iotcart.dto.request.MovimientoRequest;
import org.axolotlj.iotcart.dto.request.ObstaculoRequest;
import org.axolotlj.iotcart.dto.request.SecuenciaDemoRequest;
import org.axolotlj.iotcart.dto.response.IdResponse;
import org.axolotlj.iotcart.service.IotEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para las operaciones de control (escritura) del dispositivo IoT.
 * Corresponde a los SPs que insertan datos.
 */
@RestController
@RequestMapping("/api/v1/iot/control")
public class IotControlController {

    private final IotEventService iotEventService;

    /**
     * Inyecta el servicio de eventos.
     */
    @Autowired
    public IotControlController(IotEventService iotEventService) {
        this.iotEventService = iotEventService;
    }

    /**
     * Endpoint para registrar un nuevo movimiento (SP a).
     * Esto disparará una notificación WebSocket.
     */
    @PostMapping("/movimiento")
    public ResponseEntity<IdResponse> registrarMovimiento(@RequestBody MovimientoRequest request) {
        Long id = iotEventService.registrarMovimiento(request);
        return new ResponseEntity<>(new IdResponse(id), HttpStatus.CREATED);
    }

    /**
     * Endpoint para registrar un nuevo obstáculo (SP g).
     * Esto disparará una notificación WebSocket.
     */
    @PostMapping("/obstaculo")
    public ResponseEntity<IdResponse> registrarObstaculo(@RequestBody ObstaculoRequest request) {
        Long id = iotEventService.registrarObstaculo(request);
        return new ResponseEntity<>(new IdResponse(id), HttpStatus.CREATED);
    }

    /**
     * Endpoint para crear una nueva secuencia DEMO (SP d).
     */
    @PostMapping("/secuencia")
    public ResponseEntity<IdResponse> crearSecuenciaDemo(@RequestBody SecuenciaDemoRequest request) {
        Integer id = iotEventService.crearSecuenciaDemo(request);
        return new ResponseEntity<>(new IdResponse(id), HttpStatus.CREATED);
    }

    /**
     * Endpoint para ejecutar una secuencia DEMO existente (SP f).
     * Esto disparará una notificación WebSocket de "SECUENCIA_COMPLETADA".
     */
    @PostMapping("/secuencia/ejecutar")
    public ResponseEntity<Void> ejecutarSecuenciaDemo(@RequestBody EjecutarSecuenciaRequest request) {
        iotEventService.ejecutarSecuenciaDemo(request);
        return ResponseEntity.ok().build();
    }
}