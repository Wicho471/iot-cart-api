package org.axolotlj.iotcart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.sql.Timestamp;

/**
 * DTO para mapear el resultado de 'sp_obtener_ultimos_movimientos'.
 * Los nombres de campo coinciden con los alias del SP.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UltimosMovimientosDto {
    private Timestamp fecha_evento;
    private String nombre_dispositivo;
    private String movimiento;
    private String ip_cliente;
}