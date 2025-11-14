package com.example.simulacion_pasarela_pagos.dto;

public record UsuarioResponse(
        Long id,
        String nombre,
        String email,
        String rol
) {}
