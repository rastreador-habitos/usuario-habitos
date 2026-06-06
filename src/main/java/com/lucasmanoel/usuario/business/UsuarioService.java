package com.lucasmanoel.usuario.business;

import com.lucasmanoel.usuario.business.converter.UsuarioConverter;
import com.lucasmanoel.usuario.business.dto.UsuarioDTO;
import com.lucasmanoel.usuario.business.dto.UsuarioDTOResponse;
import com.lucasmanoel.usuario.business.dto.UsuarioLoginRequest;
import com.lucasmanoel.usuario.infrastructure.entity.UsuarioEntity;
import com.lucasmanoel.usuario.infrastructure.exceptions.ConflictException;
import com.lucasmanoel.usuario.infrastructure.exceptions.ResourceNotFoundException;
import com.lucasmanoel.usuario.infrastructure.exceptions.UnauthorizedException;
import com.lucasmanoel.usuario.infrastructure.repository.UsuarioRepository;
import com.lucasmanoel.usuario.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service

public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioConverter usuarioConverter;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    String emailNaoEncontrado = "Email não encontrado";

    public UsuarioDTOResponse cadastraUsuario(UsuarioDTO dto) {
        emailExiste(dto.getEmail());
        dto.setSenha(passwordEncoder.encode(dto.getSenha()));
        UsuarioEntity entity = usuarioConverter.paraUsuarioEntity(dto);
        usuarioRepository.save(entity);
        return usuarioConverter.paraUsuarioDTOResponse(entity);

    }

    public String login(UsuarioLoginRequest dto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.email(), dto.senha())
            );
            return "Bearer " + jwtUtil.generateToken(authentication.getName());
        } catch (BadCredentialsException | UsernameNotFoundException | AuthorizationDeniedException e) {
            throw new UnauthorizedException("Usuario ou senha inválidos: ", e.getCause());
        }
    }

    private void emailExiste(String email) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new ConflictException("Email já cadastrado");
        }
    }


    public UsuarioDTOResponse buscaUsuarioPorEmail(String token, String email) {
        UsuarioEntity entity = usuarioRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException(emailNaoEncontrado)
        );
        if (!jwtUtil.extrairEmailToken(token.substring(7)).equals(entity.getEmail())) {
            throw new UnauthorizedException("Usuario não autenticado");
        }
        return usuarioConverter.paraUsuarioDTOResponse(entity);
    }

    public void deletaUsuario(String token, String email) {
        UsuarioEntity entity = usuarioRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException(emailNaoEncontrado)
        );
        if (!jwtUtil.extrairEmailToken(token.substring(7)).equals(entity.getEmail())) {
            throw new UnauthorizedException("Usuario não autenticado");
        }
        usuarioRepository.deleteByEmail(email);
    }

    public UsuarioDTOResponse alteraUsuario(String token, UsuarioDTO dto) {
        String email = jwtUtil.extrairEmailToken(token.substring(7));
        UsuarioEntity entity = usuarioRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException(emailNaoEncontrado)
        );
        UsuarioEntity atualizada = usuarioConverter.alterarUsuario(dto, entity);
        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            atualizada.setSenha(passwordEncoder.encode(dto.getSenha()));
        }
        return usuarioConverter.paraUsuarioDTOResponse(usuarioRepository.save(atualizada));
    }


}
