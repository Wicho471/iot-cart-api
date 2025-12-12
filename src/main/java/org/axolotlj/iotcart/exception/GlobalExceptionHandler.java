package org.axolotlj.iotcart.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.axolotlj.iotcart.dto.response.ApiResponse;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * Manejador global de excepciones para la aplicación.
 * Captura excepciones no controladas y devuelve una respuesta HTTP estandarizada
 * usando el formato ApiResponse.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LogManager.getLogger(GlobalExceptionHandler.class);

    /**
     * Maneja excepciones de acceso a datos (JPA, JDBC).
     *
     * @param ex La excepción de acceso a datos.
     * @param request La solicitud web actual.
     * @return Una respuesta HTTP 500 (Internal Server Error) en formato ApiResponse.
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataAccessException(DataAccessException ex, WebRequest request) {
        // Regla f: Registrar detalladamente la excepción
        log.error("Excepción de acceso a datos [DataAccessException] en la solicitud: {}. Causa raíz: {}",
                request.getDescription(false), ex.getMostSpecificCause().getMessage(), ex);

        // Crear respuesta de error estándar
        String errorMessage = "Ocurrió un error al procesar la solicitud en la base de datos.";
        ApiResponse<Object> apiResponse = ApiResponse.error(errorMessage);
        
        // No exponer detalles de la BD al cliente
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Maneja todas las demás excepciones genéricas no controladas.
     *
     * @param ex La excepción genérica.
     * @param request La solicitud web actual.
     * @return Una respuesta HTTP 500 (Internal Server Error) en formato ApiResponse.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGlobalException(Exception ex, WebRequest request) {
        // Regla f: Registrar detalladamente la excepción
        log.error("Excepción genérica no controlada [Exception] en la solicitud: {}",
                request.getDescription(false), ex);

        // Crear respuesta de error estándar
        String errorMessage = "Ocurrió un error inesperado.";
        ApiResponse<Object> apiResponse = ApiResponse.error(errorMessage);
        
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}