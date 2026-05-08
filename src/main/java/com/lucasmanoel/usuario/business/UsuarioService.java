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
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

@Controller
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Service

public class UsuarioService {

    private final UsuarioService usuarioservice;
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
        return usuarioRepository.existesByEmail(email);
    }

    public UsuarioDTO buscaUsuarioPorEmail(String email){
        return usuarioConverter.parausuarioDTO(usuarioRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException(emailNaoEncontrado)
        ));
    }

    public void deletaUsuarioPorEmail(String email){
        usuarioRepository.deleteByEmail(email);
    }

    public UsuarioDTO alteraUsuario(String token, UsuarioDTO dto){
        String email = jwtUtil.extrairEmailToken(token.substring(7));
        dto.setSenha(dto.getSenha() != null ? passwordEncoder.encode(dto.getSenha()) : null);
        UsuarioEntity entity = usuarioRepository.findByEmail(email).orElseThrow(
                () ->  new ResourceNotFoundException(emailNaoEncontrado)
        );
        UsuarioEntity entity2 = usuarioConverter.alterarUsuario(dto, entity);
        return usuarioConverter.parausuarioDTO(usuarioRepository.save(entity2));
    }


}
