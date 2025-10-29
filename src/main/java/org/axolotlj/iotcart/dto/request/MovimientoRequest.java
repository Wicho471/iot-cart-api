package org.axolotlj.iotcart.dto.request;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO para la solicitud de 'sp_agregar_movimiento'.
 */
@Getter
@Setter
public class MovimientoRequest extends EventoBaseRequest {
    private String codigoOperacion;
}