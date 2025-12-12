package org.axolotlj.iotcart.websocket;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * Maneja las conexiones WebSocket para el monitoreo de eventos IoT en tiempo
 * real.
 */
@Component
public class IotEventSocketHandler extends TextWebSocketHandler {

	private static final Logger log = LogManager.getLogger(IotEventSocketHandler.class);

	// Lista segura para concurrencia de sesiones (monitores conectados)
	private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

	/**
	 * Se invoca después de que se establece una conexión WebSocket.
	 *
	 * @param session La sesión del cliente que se ha conectado.
	 */
	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		sessions.add(session);
		log.info("Nueva conexión WebSocket establecida: [ID: {}, RemoteAddress: {}]", session.getId(),
				session.getRemoteAddress());
	}

	/**
	 * Se invoca después de que se cierra una conexión WebSocket.
	 *
	 * @param session La sesión que se ha cerrado.
	 * @param status  El estado de cierre.
	 */
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		sessions.remove(session);
		log.info("Conexión WebSocket cerrada: [ID: {}, Status: {}]", session.getId(), status);
	}

	/**
	 * Maneja errores en la conexión WebSocket.
	 *
	 * @param session   La sesión donde ocurrió el error.
	 * @param exception La excepción lanzada.
	 */
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) {
		if (exception instanceof ClosedChannelException) {
			log.warn("Ws cerrado incorrectamente");
			return;
		}
		log.error("Error de transporte en WebSocket: [ID: {}]", session.getId());
	}

	/**
	 * Envía un mensaje de texto (push) a todos los clientes (monitores) conectados.
	 *
	 * @param message El mensaje JSON a transmitir.
	 */
	public void broadcast(String message) {
		if (sessions.isEmpty()) {
			log.debug("No hay sesiones de monitoreo activas para transmitir.");
			return;
		}

		TextMessage textMessage = new TextMessage(message);
		for (WebSocketSession session : sessions) {
			try {
				if (session.isOpen()) {
					session.sendMessage(textMessage);
				}
			} catch (IOException e) {
				log.warn("No se pudo enviar mensaje a la sesión WebSocket: [ID: {}]", session.getId(), e);
			}
		}
	}
}