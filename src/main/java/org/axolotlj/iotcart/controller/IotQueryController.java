package org.axolotlj.iotcart.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.axolotlj.iotcart.dto.response.UltimasSecuenciasDto;
import org.axolotlj.iotcart.dto.response.UltimosMovimientosDto;
import org.axolotlj.iotcart.dto.response.UltimosObstaculosDto;
import org.axolotlj.iotcart.service.IotEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para las operaciones de consulta (lectura) del dispositivo
 * IoT. Corresponde a los SPs que obtienen datos.
 */
@RestController
@RequestMapping("/api/v1/iot/query")
public class IotQueryController {

	private static final Logger log = LogManager.getLogger(IotQueryController.class);

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
	public ResponseEntity<List<UltimosMovimientosDto>> getUltimosMovimientos(@PathVariable String dispositivoNombre,
			@RequestParam(defaultValue = "10") int limite) {

		log.info("Endpoint /movimiento/{} invocado (limite: {})", dispositivoNombre, limite);
		List<UltimosMovimientosDto> movimientos = iotEventService.obtenerUltimosMovimientos(dispositivoNombre, limite);

		log.debug("Consulta de movimientos para {} devolvió {} resultados.", dispositivoNombre, movimientos.size());
		return ResponseEntity.ok(movimientos);
	}

	/**
	 * Endpoint para obtener los últimos obstáculos (SP h, i).
	 */
	@GetMapping("/obstaculo/{dispositivoNombre}")
	public ResponseEntity<List<UltimosObstaculosDto>> getUltimosObstaculos(@PathVariable String dispositivoNombre,
			@RequestParam(defaultValue = "10") int limite) {

		log.info("Endpoint /obstaculo/{} invocado (limite: {})", dispositivoNombre, limite);
		List<UltimosObstaculosDto> obstaculos = iotEventService.obtenerUltimosObstaculos(dispositivoNombre, limite);

		log.debug("Consulta de obstáculos para {} devolvió {} resultados.", dispositivoNombre, obstaculos.size());
		return ResponseEntity.ok(obstaculos);
	}

	/**
	 * Endpoint para obtener las últimas secuencias DEMO (SP e).
	 */
	@GetMapping("/secuencia")
	public ResponseEntity<List<UltimasSecuenciasDto>> getUltimasSecuenciasDemo() {
		log.info("Endpoint /secuencia (consulta) invocado.");
		List<UltimasSecuenciasDto> secuencias = iotEventService.obtenerUltimasSecuenciasDemo();

		log.debug("Consulta de secuencias DEMO devolvió {} resultados.", secuencias.size());
		return ResponseEntity.ok(secuencias);
	}
}