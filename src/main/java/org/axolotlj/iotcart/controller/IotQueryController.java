package org.axolotlj.iotcart.controller;

import java.util.Arrays; // Importar
import java.util.List;
import java.util.stream.Collectors; // Importar

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.axolotlj.iotcart.dto.response.ApiResponse;
import org.axolotlj.iotcart.dto.response.SecuenciaResponse; // Importar nuevo DTO
import org.axolotlj.iotcart.dto.response.UltimasSecuenciasDto;
import org.axolotlj.iotcart.dto.response.UltimosMovimientosDto;
import org.axolotlj.iotcart.dto.response.UltimosObstaculosDto;
import org.axolotlj.iotcart.service.IotEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@CrossOrigin(origins = "https://wicho471.github.io")
@RequestMapping("/api/v1/iot/query")
public class IotQueryController {

	private static final Logger log = LogManager.getLogger(IotQueryController.class);

	private final IotEventService iotEventService;

	@Autowired
	public IotQueryController(IotEventService iotEventService) {
		this.iotEventService = iotEventService;
	}

	/**
	 * Endpoint para obtener los últimos movimientos (SP b, c).
	 * Devuelve la lista en el formato ApiResponse.
	 */
	@GetMapping("/movimiento/{dispositivoNombre}")
	public ResponseEntity<ApiResponse<List<UltimosMovimientosDto>>> getUltimosMovimientos(
			@PathVariable String dispositivoNombre,
			@RequestParam(defaultValue = "10") int limite) {

		log.info("Endpoint /movimiento/{} invocado (limite: {})", dispositivoNombre, limite);
		List<UltimosMovimientosDto> movimientos = iotEventService.obtenerUltimosMovimientos(dispositivoNombre, limite);
		log.debug("Consulta de movimientos para {} devolvió {} resultados.", dispositivoNombre, movimientos.size());

		ApiResponse<List<UltimosMovimientosDto>> response = ApiResponse.success(movimientos);
		return ResponseEntity.ok(response);
	}

	/**
	 * Endpoint para obtener los últimos obstáculos (SP h, i).
	 * Devuelve la lista en el formato ApiResponse.
	 */
	@GetMapping("/obstaculo/{dispositivoNombre}")
	public ResponseEntity<ApiResponse<List<UltimosObstaculosDto>>> getUltimosObstaculos(
			@PathVariable String dispositivoNombre,
			@RequestParam(defaultValue = "10") int limite) {

		log.info("Endpoint /obstaculo/{} invocado (limite: {})", dispositivoNombre, limite);
		List<UltimosObstaculosDto> obstaculos = iotEventService.obtenerUltimosObstaculos(dispositivoNombre, limite);
		log.debug("Consulta de obstáculos para {} devolvió {} resultados.", dispositivoNombre, obstaculos.size());
		
		ApiResponse<List<UltimosObstaculosDto>> response = ApiResponse.success(obstaculos);
		return ResponseEntity.ok(response);
	}

	/**
	 * Endpoint para obtener las últimas secuencias DEMO (SP e).
	 * Devuelve la lista en el formato ApiResponse, transformando los
	 * pasos de String a un Arreglo.
	 */
	@GetMapping("/secuencia")
	public ResponseEntity<ApiResponse<List<SecuenciaResponse>>> getUltimasSecuenciasDemo() {
		log.info("Endpoint /secuencia (consulta) invocado.");
		
		// 1. Obtener DTO de la DB (con pasos como String)
		List<UltimasSecuenciasDto> secuenciasDto = iotEventService.obtenerUltimasSecuenciasDemo();
		log.debug("Consulta de secuencias DEMO devolvió {} resultados.", secuenciasDto.size());

		// 2. Transformar la lista
		List<SecuenciaResponse> secuenciasResponse = secuenciasDto.stream()
				.map(dto -> new SecuenciaResponse(
						dto.getId_secuencia(),
						dto.getNombre_secuencia(),
						dto.getFecha_creacion(),
						dto.getNumero_pasos(),
						// Transformación: Separar el string por " -> "
						Arrays.asList(dto.getPasos().split(" -> "))
				))
				.collect(Collectors.toList());

		// 3. Envolver la respuesta transformada
		ApiResponse<List<SecuenciaResponse>> response = ApiResponse.success(secuenciasResponse);
		return ResponseEntity.ok(response);
	}
}