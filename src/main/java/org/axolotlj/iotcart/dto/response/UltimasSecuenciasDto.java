package org.axolotlj.iotcart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.sql.Timestamp;

/**
 * DTO para mapear el resultado de 'sp_obtener_ultimas_secuencias_demo'.
 * Los nombres de campo coinciden con los alias del SP.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UltimasSecuenciasDto {
    private int id_secuencia;
    private String nombre_secuencia;
    private Timestamp fecha_creacion;
    private long numero_pasos; // COUNT(*) suele ser BIGINT
    private String pasos;
}