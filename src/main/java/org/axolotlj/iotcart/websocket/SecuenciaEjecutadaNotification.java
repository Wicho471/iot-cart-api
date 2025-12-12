package org.axolotlj.iotcart.websocket;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO (Payload) para la notificación WebSocket 'SECUENCIA_COMPLETADA'.
 * Contiene los pasos mapeados a sus IDs numéricos.
 */
@Getter
@Setter
public class SecuenciaEjecutadaNotification {

    // --- Datos de la Secuencia ---
    private int idSecuencia;
    private long numeroPasos;
    
    /**
     * Lista de pasos representados por sus IDs numéricos.
     */
    private List<Integer> pasos;

    // --- Datos de la Ejecución ---
    private int velocidad;
    private String nombreDispositivo;
    private String pais;
    private String ciudad;
    private BigDecimal latitud;
    private BigDecimal longitud;

    /**
     * Constructor para poblar los datos.
     *
     * @param idSecuencia       El ID de la secuencia.
     * @param numeroPasos       El conteo total de pasos.
     * @param pasos             La lista de pasos (IDs).
     * @param velocidad         La velocidad de ejecución.
     * @param nombreDispositivo El dispositivo que la ejecutó.
     */
    public SecuenciaEjecutadaNotification(int idSecuencia, long numeroPasos, List<Integer> pasos, int velocidad, String nombreDispositivo) {
        this.idSecuencia = idSecuencia;
        this.numeroPasos = numeroPasos;
        this.pasos = pasos;
        this.velocidad = velocidad;
        this.nombreDispositivo = nombreDispositivo;
    }
}