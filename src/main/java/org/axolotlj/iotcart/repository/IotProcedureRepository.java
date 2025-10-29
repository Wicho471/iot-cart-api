package org.axolotlj.iotcart.repository;

import org.axolotlj.iotcart.dto.response.UltimasSecuenciasDto;
import org.axolotlj.iotcart.dto.response.UltimosMovimientosDto;
import org.axolotlj.iotcart.dto.response.UltimosObstaculosDto;
import org.axolotlj.iotcart.model.BitacoraEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repositorio de Spring Data JPA para interactuar con los procedimientos
 * almacenados de la base de datos 'iot_dispositivo_db'.
 */
@Repository
public interface IotProcedureRepository extends JpaRepository<BitacoraEvento, Long> {

    /**
     * Llama a 'sp_agregar_movimiento' (Índice a).
     */
    @Procedure(procedureName = "sp_agregar_movimiento", outputParameterName = "id_evento_creado")
    Long agregarMovimiento(
            @Param("in_nombre_dispositivo") String nombreDispositivo,
            @Param("in_codigo_operacion") String codigoOperacion,
            @Param("in_ip_cliente") String ipCliente,
            @Param("in_pais") String pais,
            @Param("in_ciudad") String ciudad,
            @Param("in_latitud") BigDecimal latitud,
            @Param("in_longitud") BigDecimal longitud
    );

    /**
     * Llama a 'sp_agregar_obstaculo' (Índice g).
     */
    @Procedure(procedureName = "sp_agregar_obstaculo", outputParameterName = "id_evento_creado")
    Long agregarObstaculo(
            @Param("in_nombre_dispositivo") String nombreDispositivo,
            @Param("in_codigo_obstaculo") String codigoObstaculo,
            @Param("in_ip_cliente") String ipCliente,
            @Param("in_pais") String pais,
            @Param("in_ciudad") String ciudad,
            @Param("in_latitud") BigDecimal latitud,
            @Param("in_longitud") BigDecimal longitud
    );

    /**
     * Llama a 'sp_obtener_ultimos_movimientos' (Índice b, c).
     * Mapea el resultado a UltimosMovimientosDto.
     */
    @Procedure(procedureName = "sp_obtener_ultimos_movimientos")
    List<UltimosMovimientosDto> obtenerUltimosMovimientos(
            @Param("in_nombre_dispositivo") String nombreDispositivo,
            @Param("in_limite") int limite
    );

    /**
     * Llama a 'sp_obtener_ultimos_obstaculos' (Índice h, i).
     * Mapea el resultado a UltimosObstaculosDto.
     */
    @Procedure(procedureName = "sp_obtener_ultimos_obstaculos")
    List<UltimosObstaculosDto> obtenerUltimosObstaculos(
            @Param("in_nombre_dispositivo") String nombreDispositivo,
            @Param("in_limite") int limite
    );

    /**
     * Llama a 'sp_agregar_secuencia_demo' (Índice d).
     */
    @Procedure(procedureName = "sp_agregar_secuencia_demo", outputParameterName = "id_secuencia_creada")
    Integer agregarSecuenciaDemo(
            @Param("in_nombre_secuencia") String nombreSecuencia,
            @Param("in_cantidad_movimientos") int cantidadMovimientos
    );

    /**
     * Llama a 'sp_ejecutar_secuencia_demo' (Índice f).
     * Este SP no devuelve los pasos, solo un mensaje de éxito.
     */
    @Procedure(procedureName = "sp_ejecutar_secuencia_demo")
    void ejecutarSecuenciaDemo(
            @Param("in_id_secuencia") int idSecuencia,
            @Param("in_nombre_dispositivo") String nombreDispositivo,
            @Param("in_ip_cliente") String ipCliente,
            @Param("in_pais") String pais,
            @Param("in_ciudad") String ciudad,
            @Param("in_latitud") BigDecimal latitud,
            @Param("in_longitud") BigDecimal longitud
    );

    /**
     * Llama a 'sp_obtener_ultimas_secuencias_demo' (Índice e).
     * Mapea el resultado a UltimasSecuenciasDto.
     */
    @Procedure(procedureName = "sp_obtener_ultimas_secuencias_demo")
    List<UltimasSecuenciasDto> obtenerUltimasSecuenciasDemo();
}