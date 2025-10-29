package org.axolotlj.iotcart.dto.request;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO para la solicitud de 'sp_agregar_obstaculo'.
 */
@Getter
@Setter
public class ObstaculoRequest extends EventoBaseRequest {
    private String codigoObstaculo;
}