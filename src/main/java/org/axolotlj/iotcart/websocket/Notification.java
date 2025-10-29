package org.axolotlj.iotcart.websocket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Representa un mensaje de notificación estándar para enviar vía WebSocket.
 *
 * @param <T> El tipo de dato (payload) que contiene la notificación.
 */
@Getter
@Setter
@AllArgsConstructor
public class Notification<T> {

	/**
	 * Define el tipo de evento (ej. "NUEVO_MOVIMIENTO", "NUEVO_OBSTACULO").
	 */
	private String eventType;

	/**
	 * Los datos asociados con el evento (el DTO del movimiento, obstáculo, etc.).
	 */
	private T payload;
}