package com.example.simulacion_pasarela_pagos.controller;

import com.example.simulacion_pasarela_pagos.dto.UsuarioResponse;
import com.example.simulacion_pasarela_pagos.model.Usuario;
import com.example.simulacion_pasarela_pagos.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listar() {
        List<UsuarioResponse> resp = usuarioRepository.findAll()
                .stream()
                .map(u -> new UsuarioResponse(u.getId(), u.getNombre(), u.getEmail(), u.getRol().name()))
                .toList();
        return ResponseEntity.ok(resp);
    }
}
