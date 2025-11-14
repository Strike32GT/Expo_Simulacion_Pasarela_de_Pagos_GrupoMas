package com.example.simulacion_pasarela_pagos.service;

import com.example.simulacion_pasarela_pagos.dto.PagoRequest;
import com.example.simulacion_pasarela_pagos.dto.PagoResponse;
import com.example.simulacion_pasarela_pagos.model.Pago;
import com.example.simulacion_pasarela_pagos.model.Usuario;
import com.example.simulacion_pasarela_pagos.repository.PagoRepository;
import com.example.simulacion_pasarela_pagos.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final UsuarioRepository usuarioRepository;

    public PagoService(PagoRepository pagoRepository, UsuarioRepository usuarioRepository) {
        this.pagoRepository = pagoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public PagoResponse crearPago(PagoRequest req, String emailOrigen) {
        Usuario origen;
        if (req.origenId() != null) {
            origen = usuarioRepository.findById(req.origenId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario origen no encontrado"));
        } else {
            origen = usuarioRepository.findByEmail(emailOrigen)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario origen no encontrado"));
        }
        Usuario destino;
        if (req.destinoId() != null) {
            destino = usuarioRepository.findById(req.destinoId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario destino no encontrado"));
        } else {
            destino = usuarioRepository.findFirstByRolOrderByIdAsc(Usuario.Rol.ADMIN)
                    .orElseThrow(() -> new IllegalArgumentException("No hay usuario ADMIN para asignar como destino por defecto"));
        }

        Pago.Metodos metodo;
        try {
            metodo = Pago.Metodos.valueOf(req.metodo().toUpperCase());
        } catch (Exception ex) {
            throw new IllegalArgumentException("Método inválido. Use TARJETA, YAPE o PAYPAL");
        }

        Pago pago = new Pago();
        pago.setOrigen(origen);
        pago.setDestino(destino);
        pago.setMonto(req.monto());
        pago.setMetodo(metodo);
        pago.setEstado(Pago.EstadoPago.APROBADO);

        pago = pagoRepository.save(pago);

        // Generar firma digital (SHA-256) basada en campos críticos
        String payload = String.join("|",
                String.valueOf(pago.getId()),
                String.valueOf(origen.getId()),
                String.valueOf(destino.getId()),
                pago.getMonto().toPlainString(),
                pago.getMetodo().name()
        );
        String firma = sha256(payload);
        pago.setFirmaDigital("SHA256-" + firma);
        pago = pagoRepository.save(pago);

        // Reload to ensure DB-generated fields like fecha are populated
        pago = pagoRepository.findById(pago.getId()).orElse(pago);

        return new PagoResponse(
                pago.getId(),
                pago.getMonto(),
                pago.getMetodo().name(),
                pago.getEstado().name(),
                pago.getOrigen().getId(),
                pago.getDestino().getId(),
                pago.getFecha()
        );
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                String h = Integer.toHexString(0xff & b);
                if (h.length() == 1) hex.append('0');
                hex.append(h);
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }
}
