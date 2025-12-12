package org.axolotlj.iotcart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.List;

/**
 * DTO (Clase) para la respuesta de la API de secuencias.
 * Transforma el string 'pasos' en un arreglo.
 */
@Getter
@AllArgsConstructor
public class SecuenciaResponse {

    private int id_secuencia;
    private String nombre_secuencia;
    private Timestamp fecha_creacion;
    private long numero_pasos;
    
    /**
     * Los pasos de la secuencia, como un arreglo de strings.
     */
    private List<String> pasos;
}