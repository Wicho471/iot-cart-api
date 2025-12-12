package org.axolotlj.iotcart.util;

import java.util.Arrays;
import java.util.Optional;

/**
 * Enumerador que mapea los códigos numéricos de operación con sus representaciones de texto.
 * Soporta tanto la descripción legible (Base de Datos) como el código de protocolo (Frontend).
 */
public enum OperacionEnum {
    // ID, Descripción (DB/Humano), Código Protocolo (Frontend)
    ADELANTE(1, "Adelante", "ADELANTE"),
    ATRAS(2, "Atrás", "ATRAS"),
    DETENER(3, "Detener", "DETENER"),
    
    VUELTA_ADELANTE_DERECHA(4, "Vuelta adelante derecha", "VUELTA_ADELANTE_DER"),
    VUELTA_ADELANTE_IZQUIERDA(5, "Vuelta adelante izquierda", "VUELTA_ADELANTE_IZQ"),
    
    VUELTA_ATRAS_DERECHA(6, "Vuelta atrás derecha", "VUELTA_ATRAS_DER"),
    VUELTA_ATRAS_IZQUIERDA(7, "Vuelta atrás izquierda", "VUELTA_ATRAS_IZQ"),
    
    GIRO_90_DERECHA(8, "Giro 90° derecha", "GIRO_90_DER"),
    GIRO_90_IZQUIERDA(9, "Giro 90° izquierda", "GIRO_90_IZQ"),
    
    GIRO_360_DERECHA(10, "Giro 360° derecha", "GIRO_360_DER"),
    GIRO_360_IZQUIERDA(11, "Giro 360° izquierda", "GIRO_360_IZQ");

    private final int id;
    private final String descripcion;
    private final String codigoProtocolo;

    /**
     * Constructor del enumerador.
     *
     * @param id              El identificador numérico de la operación.
     * @param descripcion     El texto descriptivo (usualmente de la BD).
     * @param codigoProtocolo El código de texto enviado por el cliente (Frontend).
     */
    OperacionEnum(int id, String descripcion, String codigoProtocolo) {
        this.id = id;
        this.descripcion = descripcion;
        this.codigoProtocolo = codigoProtocolo;
    }

    public int getId() {
        return id;
    }

    /**
     * Busca el ID de una operación comparando contra la descripción, el código de protocolo o el nombre del enum.
     * Es insensible a mayúsculas y minúsculas.
     *
     * @param texto El texto a buscar (ej. "VUELTA_ADELANTE_DER" o "Vuelta adelante derecha").
     * @return Un Optional con el ID numérico si se encuentra coincidencia.
     */
    public static Optional<Integer> obtenerIdPorTexto(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return Optional.empty();
        }
        String textoNormalizado = texto.trim();

        return Arrays.stream(values())
                .filter(op -> 
                    // 1. Comparar con el código de protocolo (Prioridad Frontend: "VUELTA_ADELANTE_DER")
                    op.codigoProtocolo.equalsIgnoreCase(textoNormalizado) ||
                    // 2. Comparar con la descripción (Prioridad BD: "Vuelta adelante derecha")
                    op.descripcion.equalsIgnoreCase(textoNormalizado) ||
                    // 3. Comparar con el nombre de la constante (Fallback)
                    op.name().equalsIgnoreCase(textoNormalizado)
                )
                .map(OperacionEnum::getId)
                .findFirst();
    }
}