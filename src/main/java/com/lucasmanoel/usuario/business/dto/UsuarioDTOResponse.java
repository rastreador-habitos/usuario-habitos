package com.lucasmanoel.usuario.business.dto;

import lombok.Builder;

@Builder
public record UsuarioDTOResponse (String nome, String email){
}
