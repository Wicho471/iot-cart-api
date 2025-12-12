package org.axolotlj.iotcart.websocket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO específico para la notificación WebSocket de 'NUEVO_MOVIMIENTO'.
 * A diferencia del Request, este utiliza un entero para el código de operación.
 */
@Getter
@Setter
@AllArgsConstructor
public class MovimientoNotification {
    private String nombreDispositivo;
    private String pais;
    private String ciudad;
    private BigDecimal latitud;
    private BigDecimal longitud;
    
    /**
     * Código numérico de la operación (ej. 1 en lugar de "ADELANTE").
     */
    private int codigoOperacion;
    
    private int velocidad;
}