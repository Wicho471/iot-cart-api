package org.axolotlj.iotcart.dto.request;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO para la solicitud de 'sp_agregar_secuencia_demo'.
 */
@Getter
@Setter
public class SecuenciaDemoRequest {
    private String nombreSecuencia;
    private int cantidadMovimientos;
}