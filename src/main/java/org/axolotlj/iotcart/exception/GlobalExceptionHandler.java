package org.axolotlj.iotcart.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para la aplicación.
 * Captura excepciones no controladas y devuelve una respuesta HTTP estandarizada.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LogManager.getLogger(GlobalExceptionHandler.class);

    /**
     * Maneja excepciones de acceso a datos (JPA, JDBC).
     *
     * @param ex La excepción de acceso a datos.
     * @param request La solicitud web actual.
     * @return Una respuesta HTTP 500 (Internal Server Error).
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, String>> handleDataAccessException(DataAccessException ex, WebRequest request) {
        // Regla f: Registrar detalladamente la excepción
        log.error("Excepción de acceso a datos [DataAccessException] en la solicitud: {}. Causa raíz: {}",
                request.getDescription(false), ex.getMostSpecificCause().getMessage(), ex);

        Map<String, String> response = new HashMap<>();
        response.put("error", "Error de base de datos.");
        response.put("mensaje", "Ocurrió un error al procesar la solicitud en la base de datos.");
        
        // No exponer detalles de la BD al cliente
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Maneja todas las demás excepciones genéricas no controladas.
     *
     * @param ex La excepción genérica.
     * @param request La solicitud web actual.
     * @return Una respuesta HTTP 500 (Internal Server Error).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGlobalException(Exception ex, WebRequest request) {
        // Regla f: Registrar detalladamente la excepción
        log.error("Excepción genérica no controlada [Exception] en la solicitud: {}",
                request.getDescription(false), ex);

        Map<String, String> response = new HashMap<>();
        response.put("error", "Error interno del servidor.");
        response.put("mensaje", "Ocurrió un error inesperado.");
        
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}