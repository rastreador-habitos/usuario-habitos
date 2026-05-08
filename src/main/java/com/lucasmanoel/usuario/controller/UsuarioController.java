package com.lucasmanoel.usuario.controller;

import com.lucasmanoel.usuario.business.UsuarioService;
import com.lucasmanoel.usuario.business.dto.UsuarioDTO;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioDTO> cadastraUsuario(@RequestBody UsuarioDTO dto){
        return ResponseEntity.ok(usuarioService.cadastraUsuario(dto));
    }

    @PutMapping
    public ResponseEntity<UsuarioDTO> alteraUsuario(@RequestHeader("Authorization") String token, @RequestBody UsuarioDTO dto){
        return ResponseEntity.ok(usuarioService.alteraUsuario(token, dto));
    }

    @GetMapping
    public ResponseEntity<UsuarioDTO> buscaUsuarioPorEmail(@RequestParam String email){
        return ResponseEntity.ok(usuarioService.buscaUsuarioPorEmail(email));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UsuarioDTO dto){
        return ResponseEntity.ok(usuarioService.login(dto));
    }

    @DeleteMapping("/{email}")
    public  ResponseEntity<Void> deletaUsuario(@PathVariable String email){
        usuarioService.deletaUsuarioPorEmail(email);
        return ResponseEntity.ok().build();
    }
}
