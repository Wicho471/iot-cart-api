package org.axolotlj.iotcart.config;

import org.axolotlj.iotcart.websocket.IotEventSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * Configuración para habilitar y registrar el manejador de WebSockets.
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final IotEventSocketHandler iotEventSocketHandler;

    /**
     * Inyecta el manejador de eventos WebSocket.
     *
     * @param iotEventSocketHandler El manejador que procesará las conexiones.
     */
    @Autowired
    public WebSocketConfig(IotEventSocketHandler iotEventSocketHandler) {
        this.iotEventSocketHandler = iotEventSocketHandler;
    }

    /**
     * Registra el manejador de WebSocket en la ruta "/ws/iot-events".
     *
     * @param registry El registro de manejadores de WebSocket.
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(iotEventSocketHandler, "/ws/iot-events")
                .setAllowedOrigins("*"); // Permitir conexiones de cualquier origen
    }
}