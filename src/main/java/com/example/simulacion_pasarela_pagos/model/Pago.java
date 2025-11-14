package com.example.simulacion_pasarela_pagos.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pago")
public class Pago {

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "origen_id", nullable = false)
    private Usuario origen;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "destino_id", nullable = false)
    private Usuario destino;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;
    

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo", nullable = false, length = 20)
    private Metodos metodo; 
    public enum Metodos {
        TARJETA,
        YAPE,
        PAYPAL
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPago estado;


    public enum EstadoPago {
        PENDIENTE,
        APROBADO,
        RECHAZADO
    }



    @Column(insertable = false, updatable = false)
    private LocalDateTime fecha;

    @Column(name = "firma_digital", length = 255)
    private String firmaDigital;

    // Manual getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getOrigen() { return origen; }
    public void setOrigen(Usuario origen) { this.origen = origen; }

    public Usuario getDestino() { return destino; }
    public void setDestino(Usuario destino) { this.destino = destino; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public Metodos getMetodo() { return metodo; }
    public void setMetodo(Metodos metodo) { this.metodo = metodo; }

    public EstadoPago getEstado() { return estado; }
    public void setEstado(EstadoPago estado) { this.estado = estado; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getFirmaDigital() { return firmaDigital; }
    public void setFirmaDigital(String firmaDigital) { this.firmaDigital = firmaDigital; }
}

