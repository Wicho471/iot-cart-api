package org.axolotlj.iotcart.service;

import org.axolotlj.iotcart.dto.request.*;
import org.axolotlj.iotcart.dto.response.*;
import java.util.List;

/**
 * Interfaz para la lógica de negocio relacionada con los eventos del dispositivo IoT.
 */
public interface IotEventService {

    /**
     * Registra un evento de movimiento y notifica vía WebSocket.
     */
    Long registrarMovimiento(MovimientoRequest request);

    /**
     * Registra un evento de obstáculo y notifica vía WebSocket.
     */
    Long registrarObstaculo(ObstaculoRequest request);

    /**
     * Ejecuta una secuencia DEMO y notifica vía WebSocket.
     */
    void ejecutarSecuenciaDemo(EjecutarSecuenciaRequest request);

    /**
     * Crea una nueva secuencia DEMO.
     */
    Integer crearSecuenciaDemo(SecuenciaDemoRequest request);

    /**
     * Obtiene los últimos N movimientos de un dispositivo.
     */
    List<UltimosMovimientosDto> obtenerUltimosMovimientos(String nombreDispositivo, int limite);

    /**
     * Obtiene los últimos N obstáculos de un dispositivo.
     */
    List<UltimosObstaculosDto> obtenerUltimosObstaculos(String nombreDispositivo, int limite);

    /**
     * Obtiene las últimas 20 secuencias DEMO creadas.
     */
    List<UltimasSecuenciasDto> obtenerUltimasSecuenciasDemo();
}