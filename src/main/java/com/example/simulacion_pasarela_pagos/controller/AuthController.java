package com.example.simulacion_pasarela_pagos.controller;

import com.example.simulacion_pasarela_pagos.dto.LoginRequest;
import com.example.simulacion_pasarela_pagos.dto.SignupRequest;
import com.example.simulacion_pasarela_pagos.model.Usuario;
import com.example.simulacion_pasarela_pagos.repository.UsuarioRepository;
import com.example.simulacion_pasarela_pagos.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService,
                          UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        UserDetails principal = (UserDetails) auth.getPrincipal();
        String token = jwtService.generateToken(principal.getUsername(), Map.of());
        return ResponseEntity.ok(Map.of("accessToken", token));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
        boolean exists = usuarioRepository.findByEmail(request.email()).isPresent();
        if (exists) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email ya registrado"));
        }
        Usuario u = new Usuario();
        u.setNombre(request.nombre());
        u.setEmail(request.email());
        u.setPassword(passwordEncoder.encode(request.password()));
        u.setRol(Usuario.Rol.CLIENTE);
        usuarioRepository.save(u);
        return ResponseEntity.ok(Map.of("message", "Usuario creado"));
    }
}
