package org.axolotlj.iotcart.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.axolotlj.iotcart.dto.request.EjecutarSecuenciaRequest;
import org.axolotlj.iotcart.dto.request.MovimientoRequest;
import org.axolotlj.iotcart.dto.request.ObstaculoRequest;
import org.axolotlj.iotcart.dto.request.SecuenciaDemoRequest;
import org.axolotlj.iotcart.dto.response.UltimasSecuenciasDto;
import org.axolotlj.iotcart.dto.response.UltimosMovimientosDto;
import org.axolotlj.iotcart.dto.response.UltimosObstaculosDto;
import org.axolotlj.iotcart.repository.IotProcedureRepository;
import org.axolotlj.iotcart.util.OperacionEnum;
import org.axolotlj.iotcart.websocket.IotEventSocketHandler;
import org.axolotlj.iotcart.websocket.MovimientoNotification;
import org.axolotlj.iotcart.websocket.Notification;
import org.axolotlj.iotcart.websocket.SecuenciaEjecutadaNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Implementación del servicio de eventos IoT. Maneja la lógica de negocio,
 * llama al repositorio y dispara notificaciones WebSocket transformadas.
 */
@Service
public class IotEventServiceImpl implements IotEventService {

	private static final Logger log = LogManager.getLogger(IotEventServiceImpl.class);

	private final IotProcedureRepository iotRepository;
	private final IotEventSocketHandler socketHandler;
	private final ObjectMapper objectMapper;

	private static final String EVENT_TYPE_MOVIMIENTO = "NUEVO_MOVIMIENTO";
	private static final String EVENT_TYPE_OBSTACULO = "NUEVO_OBSTACULO";
	private static final String EVENT_TYPE_SECUENCIA_COMPLETADA = "SECUENCIA_COMPLETADA";

	@Autowired
	public IotEventServiceImpl(IotProcedureRepository iotRepository, IotEventSocketHandler socketHandler,
			ObjectMapper objectMapper) {
		this.iotRepository = iotRepository;
		this.socketHandler = socketHandler;
		this.objectMapper = objectMapper;
	}

	@Override
	@Transactional
	public Long registrarMovimiento(MovimientoRequest request, String ipCliente) {
		log.debug("Iniciando registro de movimiento para el dispositivo: {}", request.getNombreDispositivo());
		try {
			Long eventoId = iotRepository.agregarMovimiento(request.getNombreDispositivo(),
					request.getCodigoOperacion(), ipCliente, request.getPais(), request.getCiudad(),
					request.getLatitud(), request.getLongitud());

			log.info("Movimiento registrado con éxito. Evento ID: {}", eventoId);

			// --- Transformación para WebSocket ---
			// Buscamos el ID numérico correspondiente al texto de la operación (ej.
			// "ADELANTE" -> 1)
			int operacionId = OperacionEnum.obtenerIdPorTexto(request.getCodigoOperacion()).orElse(0);

			if (operacionId == 0) {
				log.warn("No se encontró mapeo numérico para la operación: {}", request.getCodigoOperacion());
			}

			// Creamos el payload específico con el entero
			MovimientoNotification notificationPayload = new MovimientoNotification(request.getNombreDispositivo(),
					request.getPais(), request.getCiudad(), request.getLatitud(), request.getLongitud(), operacionId,
					request.getVelocidad());

			broadcastNotification(EVENT_TYPE_MOVIMIENTO, notificationPayload);
			return eventoId;

		} catch (Exception e) {
			log.error("Error inesperado al intentar registrar movimiento para [Dispositivo: {}, Operación: {}, IP: {}]",
					request.getNombreDispositivo(), request.getCodigoOperacion(), ipCliente, e);
			throw new RuntimeException("Error de base de datos al registrar movimiento.", e);
		}
	}

	@Override
	@Transactional
	public Long registrarObstaculo(ObstaculoRequest request, String ipCliente) {
		// Este método no se solicitó cambiar, se mantiene el comportamiento original
		// con el Request
		log.debug("Iniciando registro de obstáculo para el dispositivo: {}", request.getNombreDispositivo());
		try {
			Long eventoId = iotRepository.agregarObstaculo(request.getNombreDispositivo(), request.getCodigoObstaculo(),
					ipCliente, request.getPais(), request.getCiudad(), request.getLatitud(), request.getLongitud());

			log.info("Obstáculo registrado con éxito. Evento ID: {}", eventoId);
			broadcastNotification(EVENT_TYPE_OBSTACULO, request);
			return eventoId;

		} catch (Exception e) {
			log.error("Error inesperado al intentar registrar obstáculo para [Dispositivo: {}, Obstáculo: {}, IP: {}]",
					request.getNombreDispositivo(), request.getCodigoObstaculo(), ipCliente, e);
			throw new RuntimeException("Error de base de datos al registrar obstáculo.", e);
		}
	}

	@Override
	@Transactional
	public void ejecutarSecuenciaDemo(EjecutarSecuenciaRequest request, String ipCliente) {
		log.debug("Iniciando ejecución de secuencia DEMO [ID: {}] para [Dispositivo: {}]", request.getIdSecuencia(),
				request.getNombreDispositivo());

		try {
			iotRepository.ejecutarSecuenciaDemo(request.getIdSecuencia(), request.getNombreDispositivo(), ipCliente,
					request.getPais(), request.getCiudad(), request.getLatitud(), request.getLongitud());

			log.info("Secuencia DEMO [ID: {}] ejecutada en la base de datos.", request.getIdSecuencia());

			Optional<UltimasSecuenciasDto> optDto = iotRepository.obtenerUltimasSecuenciasDemo().stream()
					.filter(s -> s.getId_secuencia() == request.getIdSecuencia()).findFirst();

			long numeroPasos = 0;
			List<Integer> pasosIds = Collections.emptyList();

			if (optDto.isPresent()) {
				UltimasSecuenciasDto dto = optDto.get();
				numeroPasos = dto.getNumero_pasos();

				// --- Transformación de Strings a IDs ---
				if (dto.getPasos() != null && !dto.getPasos().isEmpty()) {
					pasosIds = Arrays.stream(dto.getPasos().split(" -> ")).map(String::trim)
							.map(OperacionEnum::obtenerIdPorTexto) // Devuelve Optional<Integer>
							.filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
				}
			} else {
				log.warn("No se pudieron encontrar los detalles de la secuencia (ID: {}) para el broadcast WebSocket.",
						request.getIdSecuencia());
			}

			SecuenciaEjecutadaNotification notificationPayload = new SecuenciaEjecutadaNotification(
					request.getIdSecuencia(), numeroPasos, pasosIds, // Pasamos la lista de Enteros
					request.getVelocidad(), request.getNombreDispositivo());

			notificationPayload.setPais(request.getPais());
			notificationPayload.setCiudad(request.getCiudad());
			notificationPayload.setLatitud(request.getLatitud());
			notificationPayload.setLongitud(request.getLongitud());

			broadcastNotification(EVENT_TYPE_SECUENCIA_COMPLETADA, notificationPayload);

		} catch (Exception e) {
			log.error("Error inesperado al intentar ejecutar secuencia DEMO [ID: {}] para [Dispositivo: {}]",
					request.getIdSecuencia(), request.getNombreDispositivo(), e);
			throw new RuntimeException("Error de base de datos al ejecutar secuencia.", e);
		}
	}

	@Override
	@Transactional
	public Integer crearSecuenciaDemo(SecuenciaDemoRequest request) {
		log.debug("Creando secuencia DEMO: {}", request.getNombreSecuencia());
		try {
			return iotRepository.agregarSecuenciaDemo(request.getNombreSecuencia(), request.getCantidadMovimientos());
		} catch (Exception e) {
			log.error("Error inesperado al crear secuencia DEMO [Nombre: {}]", request.getNombreSecuencia(), e);
			throw new RuntimeException("Error de base de datos al crear secuencia.", e);
		}
	}

	@Override
	@Transactional
	public List<UltimosMovimientosDto> obtenerUltimosMovimientos(String nombreDispositivo, int limite) {
		log.debug("Consultando últimos {} movimientos para: {}", limite, nombreDispositivo);
		try {
			return iotRepository.obtenerUltimosMovimientos(nombreDispositivo, limite);
		} catch (Exception e) {
			log.error("Error inesperado al consultar últimos movimientos [Dispositivo: {}]", nombreDispositivo, e);
			throw new RuntimeException("Error de base de datos al consultar movimientos.", e);
		}
	}

	@Override
	@Transactional
	public List<UltimosObstaculosDto> obtenerUltimosObstaculos(String nombreDispositivo, int limite) {
		log.debug("Consultando últimos {} obstáculos para: {}", limite, nombreDispositivo);
		try {
			return iotRepository.obtenerUltimosObstaculos(nombreDispositivo, limite);
		} catch (Exception e) {
			log.error("Error inesperado al consultar últimos obstáculos [Dispositivo: {}]", nombreDispositivo, e);
			throw new RuntimeException("Error de base de datos al consultar obstáculos.", e);
		}
	}

	@Override
	@Transactional
	public List<UltimasSecuenciasDto> obtenerUltimasSecuenciasDemo() {
		log.debug("Consultando últimas secuencias DEMO");
		try {
			return iotRepository.obtenerUltimasSecuenciasDemo();
		} catch (Exception e) {
			log.error("Error inesperado al consultar últimas secuencias DEMO", e);
			throw new RuntimeException("Error de base de datos al consultar secuencias.", e);
		}
	}

	private <T> void broadcastNotification(String eventType, T payload) {
		try {
			Notification<T> notification = new Notification<>(eventType, payload);
			String jsonMessage = objectMapper.writeValueAsString(notification);
			socketHandler.broadcast(jsonMessage);
			log.debug("Notificación WebSocket transmitida: {}", jsonMessage);
		} catch (JsonProcessingException e) {
			log.error("Error fatal: No se pudo serializar la notificación WebSocket de tipo [{}]", eventType, e);
		}
	}
}