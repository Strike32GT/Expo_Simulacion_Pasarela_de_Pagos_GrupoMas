package com.example.simulacion_pasarela_pagos.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PagoRequest(
        @NotNull @DecimalMin("0.01") BigDecimal monto,
        @NotBlank String metodo,
        Long origenId,
        Long destinoId
) {}
