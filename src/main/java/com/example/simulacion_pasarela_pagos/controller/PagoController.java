package com.example.simulacion_pasarela_pagos.controller;

import com.example.simulacion_pasarela_pagos.dto.PagoRequest;
import com.example.simulacion_pasarela_pagos.dto.PagoResponse;
import com.example.simulacion_pasarela_pagos.service.PagoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/pagos")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @PostMapping
    public ResponseEntity<PagoResponse> crear(@Valid @RequestBody PagoRequest request, Principal principal) {
        PagoResponse resp = pagoService.crearPago(request, principal.getName());
        return ResponseEntity.ok(resp);
    }
}
