package com.example.simulacion_pasarela_pagos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignupRequest(
        @NotBlank String nombre,
        @NotBlank @Email String email,
        @NotBlank String password
) {}
