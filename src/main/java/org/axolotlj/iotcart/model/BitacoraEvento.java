package org.axolotlj.iotcart.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

/**
 * Entidad mínima que representa la tabla 'bitacora_eventos_iot'.
 * Se utiliza principalmente como ancla para el repositorio de Spring Data JPA
 * que llamará a los procedimientos almacenados.
 */
@Entity
@Table(name = "bitacora_eventos_iot")
@Getter
@Setter
public class BitacoraEvento {

    /**
     * Clave primaria del evento.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEvento;
}