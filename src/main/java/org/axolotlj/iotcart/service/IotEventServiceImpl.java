package org.axolotlj.iotcart.service;

import java.util.List;

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
import org.axolotlj.iotcart.websocket.IotEventSocketHandler;
import org.axolotlj.iotcart.websocket.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Implementación del servicio de eventos IoT.
 * Maneja la lógica de negocio, llama al repositorio y dispara notificaciones WebSocket.
 */
@Service
public class IotEventServiceImpl implements IotEventService {

    private static final Logger log = LogManager.getLogger(IotEventServiceImpl.class);

    private final IotProcedureRepository iotRepository;
    private final IotEventSocketHandler socketHandler;
    private final ObjectMapper objectMapper; // Para serializar DTOs a JSON

    // Constantes para los tipos de eventos WebSocket
    private static final String EVENT_TYPE_MOVIMIENTO = "NUEVO_MOVIMIENTO";
    private static final String EVENT_TYPE_OBSTACULO = "NUEVO_OBSTACULO";
    private static final String EVENT_TYPE_SECUENCIA_COMPLETADA = "SECUENCIA_COMPLETADA";

    /**
     * Constructor para inyección de dependencias.
     */
    @Autowired
    public IotEventServiceImpl(IotProcedureRepository iotRepository,
                               IotEventSocketHandler socketHandler,
                               ObjectMapper objectMapper) {
        this.iotRepository = iotRepository;
        this.socketHandler = socketHandler;
        this.objectMapper = objectMapper;
    }

    /**
     * {@inheritDoc}
     * CORRECCIÓN: Firma actualizada para aceptar ipCliente
     */
    @Override
    @Transactional
    public Long registrarMovimiento(MovimientoRequest request, String ipCliente) {
        log.debug("Iniciando registro de movimiento para el dispositivo: {}", request.getNombreDispositivo());
        try {
            Long eventoId = iotRepository.agregarMovimiento(
                    request.getNombreDispositivo(),
                    request.getCodigoOperacion(),
                    ipCliente, // <-- CORRECCIÓN: Usar la IP del parámetro
                    request.getPais(),
                    request.getCiudad(),
                    request.getLatitud(),
                    request.getLongitud()
            );

            // ... (resto del método sin cambios)
            log.info("Movimiento registrado con éxito. Evento ID: {}", eventoId);
            broadcastNotification(EVENT_TYPE_MOVIMIENTO, request);
            return eventoId;

        } catch (Exception e) {
            log.error("Error inesperado al intentar registrar movimiento para [Dispositivo: {}, Operación: {}, IP: {}]",
                    request.getNombreDispositivo(), request.getCodigoOperacion(), ipCliente, e); // <-- CORRECCIÓN: Log con la IP correcta
            throw new RuntimeException("Error de base de datos al registrar movimiento.", e);
        }
    }

    /**
     * {@inheritDoc}
     * CORRECCIÓN: Firma actualizada para aceptar ipCliente
     */
    @Override
    @Transactional
    public Long registrarObstaculo(ObstaculoRequest request, String ipCliente) {
        log.debug("Iniciando registro de obstáculo para el dispositivo: {}", request.getNombreDispositivo());
        try {
            Long eventoId = iotRepository.agregarObstaculo(
                    request.getNombreDispositivo(),
                    request.getCodigoObstaculo(),
                    ipCliente, // <-- CORRECCIÓN: Usar la IP del parámetro
                    request.getPais(),
                    request.getCiudad(),
                    request.getLatitud(),
                    request.getLongitud()
            );
            
            // ... (resto del método sin cambios)
            log.info("Obstáculo registrado con éxito. Evento ID: {}", eventoId);
            broadcastNotification(EVENT_TYPE_OBSTACULO, request);
            return eventoId;

        } catch (Exception e) {
            log.error("Error inesperado al intentar registrar obstáculo para [Dispositivo: {}, Obstáculo: {}, IP: {}]",
                    request.getNombreDispositivo(), request.getCodigoObstaculo(), ipCliente, e); // <-- CORRECCIÓN: Log con la IP correcta
            throw new RuntimeException("Error de base de datos al registrar obstáculo.", e);
        }
    }

    /**
     * {@inheritDoc}
     * CORRECCIÓN: Firma actualizada para aceptar ipCliente
     */
    @Override
    @Transactional
    public void ejecutarSecuenciaDemo(EjecutarSecuenciaRequest request, String ipCliente) {
        log.debug("Iniciando ejecución de secuencia DEMO [ID: {}] para [Dispositivo: {}]",
                request.getIdSecuencia(), request.getNombreDispositivo());
        
        try {
            iotRepository.ejecutarSecuenciaDemo(
                    request.getIdSecuencia(),
                    request.getNombreDispositivo(),
                    ipCliente, // <-- CORRECCIÓN: Usar la IP del parámetro
                    request.getPais(),
                    request.getCiudad(),
                    request.getLatitud(),
                    request.getLongitud()
            );
            
            // ... (resto del método sin cambios)
            log.info("Secuencia DEMO [ID: {}] ejecutada en la base de datos.", request.getIdSecuencia());
            broadcastNotification(EVENT_TYPE_SECUENCIA_COMPLETADA, request);

        } catch (Exception e) {
            log.error("Error inesperado al intentar ejecutar secuencia DEMO [ID: {}] para [Dispositivo: {}]",
                    request.getIdSecuencia(), request.getNombreDispositivo(), e);
            throw new RuntimeException("Error de base de datos al ejecutar secuencia.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Integer crearSecuenciaDemo(SecuenciaDemoRequest request) {
        log.debug("Creando secuencia DEMO: {}", request.getNombreSecuencia());
        try {
            return iotRepository.agregarSecuenciaDemo(
                    request.getNombreSecuencia(),
                    request.getCantidadMovimientos()
            );
        } catch (Exception e) {
            log.error("Error inesperado al crear secuencia DEMO [Nombre: {}]", request.getNombreSecuencia(), e);
            throw new RuntimeException("Error de base de datos al crear secuencia.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<UltimosObstaculosDto> obtenerUltimosObstaculos(String nombreDispositivo, int limite) {
        log.debug("Consultando últimos {} obstáculos para: {}", limite, nombreDispositivo);
        try {
            return iotRepository.obtenerUltimosObstaculos(nombreDispositivo, limite);
        } catch (Exception e) {
            log.error("Error inesperado al consultar últimos obstáculos [Dispositivo: {}]", nombreDispositivo, e);
            throw new RuntimeException("Error de base de datos al consultar obstáculos.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * Método auxiliar privado para serializar y transmitir notificaciones WebSocket.
     *
     * @param eventType El tipo de evento (ej. "NUEVO_MOVIMIENTO").
     * @param payload El objeto DTO que se enviará como datos.
     */
    private <T> void broadcastNotification(String eventType, T payload) {
        try {
            Notification<T> notification = new Notification<>(eventType, payload);
            String jsonMessage = objectMapper.writeValueAsString(notification);
            socketHandler.broadcast(jsonMessage);
            log.debug("Notificación WebSocket transmitida: {}", jsonMessage);
        } catch (JsonProcessingException e) {
            // Este log es crítico, si falla la serialización, el push no funciona.
            log.error("Error fatal: No se pudo serializar la notificación WebSocket de tipo [{}]", eventType, e);
        }
    }
}