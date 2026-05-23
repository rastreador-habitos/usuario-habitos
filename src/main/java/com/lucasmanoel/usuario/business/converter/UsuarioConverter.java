package com.lucasmanoel.usuario.business.converter;

import com.lucasmanoel.usuario.business.dto.UsuarioDTO;
import com.lucasmanoel.usuario.business.dto.UsuarioDTOResponse;
import com.lucasmanoel.usuario.infrastructure.entity.UsuarioEntity;
import org.springframework.stereotype.Component;

@Component
public class UsuarioConverter {

    public UsuarioDTOResponse paraUsuarioDTOResponse(UsuarioEntity entity) {
        return UsuarioDTOResponse.builder()
                .nome(entity.getNome())
                .email(entity.getEmail())
                .build();
    }

    public UsuarioEntity paraUsuarioEntity(UsuarioDTO dto){
        return UsuarioEntity.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .senha(dto.getSenha())
                .build();
    }

    public UsuarioEntity alterarUsuario(UsuarioDTO dto, UsuarioEntity entity){
        return UsuarioEntity.builder()
                .nome(dto.getNome() != null ? dto.getNome() : entity.getNome())
                .email(dto.getEmail() != null ? dto.getEmail() : entity.getEmail())
                .senha(dto.getSenha() != null ? dto.getSenha() : entity.getSenha())
                .id(entity.getId())
                .build();
    }

}
