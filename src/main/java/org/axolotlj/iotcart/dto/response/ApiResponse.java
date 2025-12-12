package org.axolotlj.iotcart.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

/**
 * Contenedor genérico estandarizado para todas las respuestas de la API.
 * Sigue el formato { success, data, error }.
 *
 * @param <T> El tipo del objeto de datos (payload) que se incluirá en la respuesta.
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL) // No incluirá campos nulos (ej. 'data' o 'error') en el JSON
public class ApiResponse<T> {

    /**
     * Indica si la operación fue exitosa.
     */
    private boolean success;

    /**
     * Los datos resultantes de la operación (payload).
     * Será nulo si la operación falló.
     */
    private T data;

    /**
     * Un mensaje de error descriptivo.
     * Será nulo si la operación fue exitosa.
     */
    private String error;

    /**
     * Constructor privado para forzar el uso de los métodos estáticos (factory methods).
     *
     * @param success Estado de éxito.
     * @param data Los datos del payload.
     * @param error El mensaje de error.
     */
    private ApiResponse(boolean success, T data, String error) {
        this.success = success;
        this.data = data;
        this.error = error;
    }

    /**
     * Crea una respuesta de API exitosa con datos.
     *
     * @param data Los datos del payload.
     * @param <T> El tipo de los datos.
     * @return Una instancia de ApiResponse marcando éxito.
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    /**
     * Crea una respuesta de API fallida con un mensaje de error.
     *
     * @param error El mensaje de error descriptivo.
     * @param <T> El tipo (genérico) de la respuesta.
     * @return Una instancia de ApiResponse marcando fallo.
     */
    public static <T> ApiResponse<T> error(String error) {
        return new ApiResponse<>(false, null, error);
    }
}