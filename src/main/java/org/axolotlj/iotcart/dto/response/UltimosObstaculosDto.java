package org.axolotlj.iotcart.dto.response;

import java.sql.Timestamp;

/**
 * DTO (Proyección de Interfaz) para mapear el resultado de 'sp_obtener_ultimos_obstaculos'.
 * Los nombres de los métodos 'get' deben coincidir con los alias de columna del SP.
 */
public interface UltimosObstaculosDto {

    /**
     * Obtiene la fecha del evento.
     * Corresponde al alias 'fecha_evento' del SP.
     *
     * @return El timestamp del evento.
     */
    Timestamp getFecha_evento();

    /**
     * Obtiene el nombre del dispositivo.
     * Corresponde al alias 'nombre_dispositivo' del SP.
     *
     * @return El nombre del dispositivo.
     */
    String getNombre_dispositivo();

    /**
     * Obtiene la descripción del obstáculo.
     * Corresponde al alias 'obstaculo_detectado' del SP.
     *
     * @return La descripción del obstáculo.
     */
    String getObstaculo_detectado();

    /**
     * Obtiene la IP del cliente.
     * Corresponde al alias 'ip_cliente' del SP.
     *
     * @return La IP del cliente.
     */
    String getIp_cliente();
}