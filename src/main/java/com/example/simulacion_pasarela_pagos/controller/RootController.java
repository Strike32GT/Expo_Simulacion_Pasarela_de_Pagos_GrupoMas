package com.example.simulacion_pasarela_pagos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController {

    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/crear_cuenta")
    public String crearCuenta() {
        return "crear_cuenta";
    }

    @GetMapping("/index")
    public String index() {
        return "index";
    }
}
