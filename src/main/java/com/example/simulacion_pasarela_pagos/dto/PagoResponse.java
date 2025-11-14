package com.example.simulacion_pasarela_pagos.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagoResponse(
        Long id,
        BigDecimal monto,
        String metodo,
        String estado,
        Long origenId,
        Long destinoId,
        LocalDateTime fecha
) {}
