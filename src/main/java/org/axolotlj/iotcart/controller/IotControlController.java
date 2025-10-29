package org.axolotlj.iotcart.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
 * Controlador REST para las operaciones de control (escritura) del dispositivo
 * IoT. Corresponde a los SPs que insertan datos.
 */
@RestController
@RequestMapping("/api/v1/iot/control")
public class IotControlController {

	private static final Logger log = LogManager.getLogger(IotControlController.class);

	private final IotEventService iotEventService;

	/**
	 * Inyecta el servicio de eventos.
	 */
	@Autowired
	public IotControlController(IotEventService iotEventService) {
		this.iotEventService = iotEventService;
	}

	/**
	 * Endpoint para registrar un nuevo movimiento (SP a). Esto disparará una
	 * notificación WebSocket.
	 */
	@PostMapping("/movimiento")
	public ResponseEntity<IdResponse> registrarMovimiento(@RequestBody MovimientoRequest request) {
		log.info("Endpoint /movimiento invocado por dispositivo: {}", request.getNombreDispositivo());
		// Usamos DEBUG para el payload completo (Regla e: detallado)
		log.debug("Payload de registrarMovimiento: {}", request::toString);

		Long id = iotEventService.registrarMovimiento(request);

		log.info("Movimiento registrado con éxito. Nuevo ID de evento: {}", id);
		return new ResponseEntity<>(new IdResponse(id), HttpStatus.CREATED);
	}

	/**
	 * Endpoint para registrar un nuevo obstáculo (SP g). Esto disparará una
	 * notificación WebSocket.
	 */
	@PostMapping("/obstaculo")
	public ResponseEntity<IdResponse> registrarObstaculo(@RequestBody ObstaculoRequest request) {
		log.info("Endpoint /obstaculo invocado por dispositivo: {}", request.getNombreDispositivo());
		log.debug("Payload de registrarObstaculo: {}", request::toString);

		Long id = iotEventService.registrarObstaculo(request);

		log.info("Obstáculo registrado con éxito. Nuevo ID de evento: {}", id);
		return new ResponseEntity<>(new IdResponse(id), HttpStatus.CREATED);
	}

	/**
	 * Endpoint para crear una nueva secuencia DEMO (SP d).
	 */
	@PostMapping("/secuencia")
	public ResponseEntity<IdResponse> crearSecuenciaDemo(@RequestBody SecuenciaDemoRequest request) {
		log.info("Endpoint /secuencia (crear) invocado.");
		log.debug("Payload de crearSecuenciaDemo: {}", request::toString);

		Integer id = iotEventService.crearSecuenciaDemo(request);

		log.info("Secuencia DEMO creada con éxito. Nuevo ID de secuencia: {}", id);
		return new ResponseEntity<>(new IdResponse(id), HttpStatus.CREATED);
	}

	/**
	 * Endpoint para ejecutar una secuencia DEMO existente (SP f). Esto disparará
	 * una notificación WebSocket de "SECUENCIA_COMPLETADA".
	 */
	@PostMapping("/secuencia/ejecutar")
	public ResponseEntity<Void> ejecutarSecuenciaDemo(@RequestBody EjecutarSecuenciaRequest request) {
		log.info("Endpoint /secuencia/ejecutar invocado para Secuencia ID: {} por Dispositivo: {}",
				request.getIdSecuencia(), request.getNombreDispositivo());
		log.debug("Payload de ejecutarSecuenciaDemo: {}", request::toString);

		iotEventService.ejecutarSecuenciaDemo(request);

		log.info("Ejecución de secuencia (ID: {}) solicitada con éxito.", request.getIdSecuencia());
		return ResponseEntity.ok().build();
	}
}