package com.lucasmanoel.usuario.business;

import com.lucasmanoel.usuario.business.converter.UsuarioConverter;
import com.lucasmanoel.usuario.business.dto.UsuarioDTO;
import com.lucasmanoel.usuario.infrastructure.entity.UsuarioEntity;
import com.lucasmanoel.usuario.infrastructure.exceptions.ConflictExeception;
import com.lucasmanoel.usuario.infrastructure.exceptions.ResourceNotFoundException;
import com.lucasmanoel.usuario.infrastructure.exceptions.UnauthorizedException;
import com.lucasmanoel.usuario.infrastructure.repository.UsuarioRepository;
import com.lucasmanoel.usuario.infrastructure.security.JwtUtil;
import lombok.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service

public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioConverter usuarioConverter;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    String emailNaoEncontrado = "Email não encontrado";

    public UsuarioDTO cadastraUsuario(UsuarioDTO dto){
        emailExiste(dto.getEmail());
        dto.setSenha(passwordEncoder.encode(dto.getSenha()));
        UsuarioEntity entity = usuarioConverter.paraUsuarioEntity(dto);
        usuarioRepository.save(entity);
        return usuarioConverter.parausuarioDTO(entity);

    }

    public String login(UsuarioDTO dto){
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getSenha())
            );
            return "Bearer " + jwtUtil.generateToken(authentication.getName());
        }catch (BadCredentialsException | UsernameNotFoundException | AuthorizationDeniedException e){
            throw new UnauthorizedException("Usuario ou senha inválidos: ", e.getCause());
        }
    }

    public void emailExiste(String email){
        try {
            if (verificaEmail(email)){
                throw new ConflictExeception("Email já cadastrado");
            }
        } catch (ConflictExeception e) {
            throw new RuntimeException(e.getCause());
        }
    }

    public boolean verificaEmail(String email){
        return usuarioRepository.existsByEmail(email);
    }

    public UsuarioDTO buscaUsuarioPorEmail(String email){
        return usuarioConverter.parausuarioDTO(usuarioRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException(emailNaoEncontrado)
        ));
    }

    public void deletaUsuarioPorEmail(String email){
        usuarioRepository.deleteByEmail(email);
    }

    public UsuarioDTO alteraUsuario(UsuarioDTO dto){
        String email = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getName)
                .orElseThrow(() -> new InsufficientAuthenticationException("Usuário não autenticado"));

        UsuarioEntity entity = usuarioRepository.findByEmail(email).orElseThrow(
                () ->  new ResourceNotFoundException(emailNaoEncontrado)
        );
        UsuarioEntity entity2 = usuarioConverter.alterarUsuario(dto, entity);
        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            entity.setSenha(passwordEncoder.encode(dto.getSenha()));
        }
        return usuarioConverter.parausuarioDTO(usuarioRepository.save(entity2));
    }


}
