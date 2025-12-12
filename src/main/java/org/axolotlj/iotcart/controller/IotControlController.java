package org.axolotlj.iotcart.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.axolotlj.iotcart.dto.request.EjecutarSecuenciaRequest;
import org.axolotlj.iotcart.dto.request.MovimientoRequest;
import org.axolotlj.iotcart.dto.request.ObstaculoRequest;
import org.axolotlj.iotcart.dto.request.SecuenciaDemoRequest;
import org.axolotlj.iotcart.dto.response.ApiResponse;
import org.axolotlj.iotcart.dto.response.IdResponse;
import org.axolotlj.iotcart.service.IotEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Controlador REST para las operaciones de control (escritura) del dispositivo
 * IoT. Corresponde a los SPs que insertan datos.
 */
@RestController
@CrossOrigin(origins = "https://wicho471.github.io")
@RequestMapping("/api/v1/iot/control")
public class IotControlController {

	private static final Logger log = LogManager.getLogger(IotControlController.class);

	private final IotEventService iotEventService;

	private static final String X_FORWARDED_FOR_HEADER = "X-Forwarded-For";

	/**
	 * Inyecta el servicio de eventos.
	 */
	@Autowired
	public IotControlController(IotEventService iotEventService) {
		this.iotEventService = iotEventService;
	}

	/**
	 * Endpoint para registrar un nuevo movimiento (SP a). Devuelve el ID del evento
	 * creado en el formato ApiResponse.
	 */
	@PostMapping("/movimiento")
	public ResponseEntity<ApiResponse<IdResponse>> registrarMovimiento(@RequestBody MovimientoRequest request,
			HttpServletRequest httpServletRequest) {

		String ipCliente = getClientIpAddress(httpServletRequest);
		log.info("Endpoint /movimiento invocado por dispositivo: {} desde IP: {}", request.getNombreDispositivo(),
				ipCliente);
		log.debug("Payload de registrarMovimiento: {}", request::toString);

		Long id = iotEventService.registrarMovimiento(request, ipCliente);
		log.info("Movimiento registrado con éxito. Nuevo ID de evento: {}", id);

		// Envolver la respuesta
		ApiResponse<IdResponse> response = ApiResponse.success(new IdResponse(id));
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	/**
	 * Endpoint para registrar un nuevo obstáculo (SP g). Devuelve el ID del evento
	 * creado en el formato ApiResponse.
	 */
	@PostMapping("/obstaculo")
	public ResponseEntity<ApiResponse<IdResponse>> registrarObstaculo(@RequestBody ObstaculoRequest request,
			HttpServletRequest httpServletRequest) {

		String ipCliente = getClientIpAddress(httpServletRequest);
		log.info("Endpoint /obstaculo invocado por dispositivo: {} desde IP: {}", request.getNombreDispositivo(),
				ipCliente);
		log.debug("Payload de registrarObstaculo: {}", request::toString);

		Long id = iotEventService.registrarObstaculo(request, ipCliente);
		log.info("Obstáculo registrado con éxito. Nuevo ID de evento: {}", id);

		// Envolver la respuesta
		ApiResponse<IdResponse> response = ApiResponse.success(new IdResponse(id));
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	/**
	 * Endpoint para crear una nueva secuencia DEMO (SP d). Devuelve el ID de la
	 * secuencia creada en el formato ApiResponse.
	 */
	@PostMapping("/secuencia")
	public ResponseEntity<ApiResponse<IdResponse>> crearSecuenciaDemo(@RequestBody SecuenciaDemoRequest request) {
		log.info("Endpoint /secuencia (crear) invocado.");
		log.debug("Payload de crearSecuenciaDemo: {}", request::toString);

		Integer id = iotEventService.crearSecuenciaDemo(request);
		log.info("Secuencia DEMO creada con éxito. Nuevo ID de secuencia: {}", id);

		// Envolver la respuesta
		ApiResponse<IdResponse> response = ApiResponse.success(new IdResponse(id));
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	/**
	 * Endpoint para ejecutar una secuencia DEMO existente (SP f). Devuelve una
	 * respuesta exitosa sin datos (solo success: true).
	 */
	@PostMapping("/secuencia/ejecutar")
	public ResponseEntity<ApiResponse<Object>> ejecutarSecuenciaDemo(@RequestBody EjecutarSecuenciaRequest request,
			HttpServletRequest httpServletRequest) {

		String ipCliente = getClientIpAddress(httpServletRequest);
		log.info("Endpoint /secuencia/ejecutar invocado para Secuencia ID: {} por Dispositivo: {} desde IP: {}",
				request.getIdSecuencia(), request.getNombreDispositivo(), ipCliente);
		log.debug("Payload de ejecutarSecuenciaDemo: {}", request::toString);

		iotEventService.ejecutarSecuenciaDemo(request, ipCliente);
		log.info("Ejecución de secuencia (ID: {}) solicitada con éxito.", request.getIdSecuencia());

		// Envolver la respuesta (éxito sin payload de datos)
		ApiResponse<Object> response = ApiResponse.success(null);
		return ResponseEntity.ok(response);
	}

	/**
	 * Método auxiliar privado para obtener la IP del cliente real.
	 */
	private String getClientIpAddress(HttpServletRequest request) {
		String ipAddress = request.getHeader(X_FORWARDED_FOR_HEADER);
		if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getRemoteAddr();
		} else {
			ipAddress = ipAddress.split(",")[0].trim();
		}
		return ipAddress;
	}
}