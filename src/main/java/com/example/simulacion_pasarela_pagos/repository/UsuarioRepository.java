package com.example.simulacion_pasarela_pagos.repository;

import com.example.simulacion_pasarela_pagos.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findFirstByRolOrderByIdAsc(Usuario.Rol rol);
}
