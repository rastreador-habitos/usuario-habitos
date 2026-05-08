package com.lucasmanoel.usuario.business.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioDTO {

    private String nome;
    private String email;
    private String senha;

}
