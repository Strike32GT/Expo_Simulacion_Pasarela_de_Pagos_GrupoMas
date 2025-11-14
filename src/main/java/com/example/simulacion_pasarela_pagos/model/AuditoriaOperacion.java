package com.example.simulacion_pasarela_pagos.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "auditoria_operaciones")
public class AuditoriaOperacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String metodo;

    @Lob
    private String parametros;

    @Lob
    private String resultado;

    @Column(name = "duracion_ms")
    private Long duracionMs;

    @Column(insertable = false, updatable = false)
    private LocalDateTime fecha;
}
