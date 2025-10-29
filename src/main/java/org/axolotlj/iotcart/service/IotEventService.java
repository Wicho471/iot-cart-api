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
     * CORRECCIÓN: Se añade ipCliente como parámetro.
     */
    Long registrarMovimiento(MovimientoRequest request, String ipCliente);

    /**
     * Registra un evento de obstáculo y notifica vía WebSocket.
     * CORRECCIÓN: Se añade ipCliente como parámetro.
     */
    Long registrarObstaculo(ObstaculoRequest request, String ipCliente);

    /**
     * Ejecuta una secuencia DEMO y notifica vía WebSocket.
     * CORRECCIÓN: Se añade ipCliente como parámetro.
     */
    void ejecutarSecuenciaDemo(EjecutarSecuenciaRequest request, String ipCliente);

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