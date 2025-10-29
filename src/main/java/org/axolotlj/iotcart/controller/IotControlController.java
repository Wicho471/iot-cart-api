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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// CORRECCIÓN: Importar HttpServletRequest
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

	// CORRECCIÓN: Variable para el header X-Forwarded-For
	private static final String X_FORWARDED_FOR_HEADER = "X-Forwarded-For";

	/**
	 * Inyecta el servicio de eventos.
	 */
	@Autowired
	public IotControlController(IotEventService iotEventService) {
		this.iotEventService = iotEventService;
	}

	/**
	 * Endpoint para registrar un nuevo movimiento (SP a). CORRECCIÓN: Se inyecta
	 * HttpServletRequest
	 */
	@PostMapping("/movimiento")
	public ResponseEntity<IdResponse> registrarMovimiento(@RequestBody MovimientoRequest request,
			HttpServletRequest httpServletRequest) {
		// CORRECCIÓN: Obtener la IP desde el backend
		String ipCliente = getClientIpAddress(httpServletRequest);

		log.info("Endpoint /movimiento invocado por dispositivo: {} desde IP: {}", request.getNombreDispositivo(),
				ipCliente);
		log.debug("Payload de registrarMovimiento: {}", request::toString);

		Long id = iotEventService.registrarMovimiento(request, ipCliente); // <-- CORRECCIÓN: Pasar la IP al servicio

		log.info("Movimiento registrado con éxito. Nuevo ID de evento: {}", id);
		return new ResponseEntity<>(new IdResponse(id), HttpStatus.CREATED);
	}

	/**
	 * Endpoint para registrar un nuevo obstáculo (SP g). CORRECCIÓN: Se inyecta
	 * HttpServletRequest
	 */
	@PostMapping("/obstaculo")
	public ResponseEntity<IdResponse> registrarObstaculo(@RequestBody ObstaculoRequest request,
			HttpServletRequest httpServletRequest) {
		// CORRECCIÓN: Obtener la IP desde el backend
		String ipCliente = getClientIpAddress(httpServletRequest);

		log.info("Endpoint /obstaculo invocado por dispositivo: {} desde IP: {}", request.getNombreDispositivo(),
				ipCliente);
		log.debug("Payload de registrarObstaculo: {}", request::toString);

		Long id = iotEventService.registrarObstaculo(request, ipCliente); // <-- CORRECCIÓN: Pasar la IP al servicio

		log.info("Obstáculo registrado con éxito. Nuevo ID de evento: {}", id);
		return new ResponseEntity<>(new IdResponse(id), HttpStatus.CREATED);
	}

	/**
	 * Endpoint para crear una nueva secuencia DEMO (SP d). (Este endpoint no
	 * registra IP, se mantiene igual)
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
	 * Endpoint para ejecutar una secuencia DEMO existente (SP f). CORRECCIÓN: Se
	 * inyecta HttpServletRequest
	 */
	@PostMapping("/secuencia/ejecutar")
	public ResponseEntity<Void> ejecutarSecuenciaDemo(@RequestBody EjecutarSecuenciaRequest request,
			HttpServletRequest httpServletRequest) {
		// CORRECCIÓN: Obtener la IP desde el backend
		String ipCliente = getClientIpAddress(httpServletRequest);

		log.info("Endpoint /secuencia/ejecutar invocado para Secuencia ID: {} por Dispositivo: {} desde IP: {}",
				request.getIdSecuencia(), request.getNombreDispositivo(), ipCliente);
		log.debug("Payload de ejecutarSecuenciaDemo: {}", request::toString);

		iotEventService.ejecutarSecuenciaDemo(request, ipCliente); // <-- CORRECCIÓN: Pasar la IP al servicio

		log.info("Ejecución de secuencia (ID: {}) solicitada con éxito.", request.getIdSecuencia());
		return ResponseEntity.ok().build();
	}

	/**
	 * Método auxiliar privado para obtener la IP del cliente real, considerando
	 * proxies (X-Forwarded-For).
	 *
	 * @param request La solicitud HTTP.
	 * @return La dirección IP del cliente.
	 */
	private String getClientIpAddress(HttpServletRequest request) {
		String ipAddress = request.getHeader(X_FORWARDED_FOR_HEADER);
		if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getRemoteAddr();
		} else {
			// X-Forwarded-For puede ser una lista: "client, proxy1, proxy2"
			ipAddress = ipAddress.split(",")[0].trim();
		}
		return ipAddress;
	}
}