package com.lucasmanoel.usuario.controller;

import com.lucasmanoel.usuario.business.UsuarioService;
import com.lucasmanoel.usuario.business.dto.UsuarioDTO;
import com.lucasmanoel.usuario.business.dto.UsuarioDTOResponse;
import com.lucasmanoel.usuario.business.dto.UsuarioLoginRequest;
import com.lucasmanoel.usuario.infrastructure.security.SecurityConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
@Tag(name = "Usuario", description = "Cadastro de usuários")
@SecurityRequirement(name = SecurityConfig.SECURITY_SCHEME)
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    @Operation(summary = "Cadastro usuario", description = "Cria um novo usuario")
    @ApiResponse(responseCode = "201", description = "Usuario cadastrado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos para a criação do usuario")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<UsuarioDTOResponse> cadastraUsuario(@RequestBody UsuarioDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.cadastraUsuario(dto));
    }

    @PutMapping
    @Operation(summary = "Altera usuario", description = "Altera dados do usuario")
    @ApiResponse(responseCode = "200", description = "Dados alterados com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<UsuarioDTOResponse> alteraUsuario(@RequestHeader("Authorization") String token, @RequestBody UsuarioDTO dto) {
        return ResponseEntity.ok(usuarioService.alteraUsuario(token, dto));
    }

    @GetMapping
    @Operation(summary = "Busca usuario", description = "Localiza usuario buscando pelo email")
    @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<UsuarioDTOResponse> buscaUsuarioPorEmail(@RequestHeader("Authorization") String token, @RequestParam String email) {
        return ResponseEntity.ok(usuarioService.buscaUsuarioPorEmail(token, email));
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Login no sistema")
    @ApiResponse(responseCode = "200", description = "Usuario logado com sucesso")
    @ApiResponse(responseCode = "401", description = "Dados inválidos")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<String> login(@RequestBody UsuarioLoginRequest dto) {
        return ResponseEntity.ok(usuarioService.login(dto));
    }

    @DeleteMapping("/{email}")
    @Operation(summary = "Deleta usuário", description = "Deleta usuário do sistema")
    @ApiResponse(responseCode = "204", description = "Usuario deletado com sucesso")
    @ApiResponse(responseCode = "403", description = "Dados inválidos")
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<Void> deletaUsuario(@RequestHeader("Authorization") String token, @PathVariable String email) {
        usuarioService.deletaUsuario(token, email);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
