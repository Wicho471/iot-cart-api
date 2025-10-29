package org.axolotlj.iotcart.dto.response;

import java.sql.Timestamp;

/**
 * DTO (Proyección de Interfaz) para mapear el resultado de 'sp_obtener_ultimas_secuencias_demo'.
 * Los nombres de los métodos 'get' deben coincidir con los alias de columna del SP.
 */
public interface UltimasSecuenciasDto {
    
    /**
     * Obtiene el ID de la secuencia.
     * Corresponde al alias 'id_secuencia' del SP.
     *
     * @return El ID de la secuencia.
     */
    int getId_secuencia();

    /**
     * Obtiene el nombre de la secuencia.
     * Corresponde al alias 'nombre_secuencia' del SP.
     *
     * @return El nombre de la secuencia.
     */
    String getNombre_secuencia();

    /**
     * Obtiene la fecha de creación.
     * Corresponde al alias 'fecha_creacion' del SP.
     *
     * @return El timestamp de creación.
     */
    Timestamp getFecha_creacion();

    /**
     * Obtiene la cantidad de pasos.
     * Corresponde al alias 'numero_pasos' del SP (COUNT).
     *
     * @return El total de pasos.
     */
    long getNumero_pasos();

    /**
     * Obtiene la cadena de pasos concatenados.
     * Corresponde al alias 'pasos' del SP (GROUP_CONCAT).
     *
     * @return Los pasos.
     */
    String getPasos();
}