package com.lucasmanoel.usuario.business.dto;

import lombok.Builder;

@Builder
public record UsuarioLoginRequest(String email, String senha) {
}
