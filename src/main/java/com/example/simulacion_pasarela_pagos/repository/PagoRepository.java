package com.example.simulacion_pasarela_pagos.repository;

import com.example.simulacion_pasarela_pagos.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    @Query("select p from Pago p where (:estado is null or p.estado = :estado)")
    List<Pago> findByEstadoNullable(@Param("estado") Pago.EstadoPago estado);
}
