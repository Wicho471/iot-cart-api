package org.axolotlj.iotcart.dto.request;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO para la solicitud de 'sp_ejecutar_secuencia_demo'.
 */
@Getter
@Setter
public class EjecutarSecuenciaRequest extends EventoBaseRequest {
    private int idSecuencia;
    private int velocidad;
}