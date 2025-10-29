package org.axolotlj.iotcart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO genérico para devolver IDs creados.
 */
@Getter
@Setter
@AllArgsConstructor
public class IdResponse {
    private Object id;
}