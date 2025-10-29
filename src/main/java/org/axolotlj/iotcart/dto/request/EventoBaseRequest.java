package org.axolotlj.iotcart.dto.request;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

/**
 * DTO base que contiene la información común para registrar un evento.
 */
@Getter
@Setter
public class EventoBaseRequest {
	private String nombreDispositivo;
	private String pais;
	private String ciudad;
	private BigDecimal latitud;
	private BigDecimal longitud;
}