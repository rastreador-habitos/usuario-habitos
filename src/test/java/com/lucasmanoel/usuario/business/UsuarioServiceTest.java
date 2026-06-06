package com.lucasmanoel.usuario.business;

import com.lucasmanoel.usuario.business.converter.UsuarioConverter;
import com.lucasmanoel.usuario.business.dto.UsuarioDTO;
import com.lucasmanoel.usuario.business.dto.UsuarioDTOResponse;
import com.lucasmanoel.usuario.business.dto.UsuarioLoginRequest;
import com.lucasmanoel.usuario.infrastructure.entity.UsuarioEntity;
import com.lucasmanoel.usuario.infrastructure.exceptions.ConflictException;
import com.lucasmanoel.usuario.infrastructure.repository.UsuarioRepository;
import com.lucasmanoel.usuario.infrastructure.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @InjectMocks
    UsuarioService usuarioService;

    @Mock
    UsuarioRepository usuarioRepository;


    @Mock
    UsuarioConverter usuarioConverter;

    @Mock
    JwtUtil jwtUtil;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    Authentication authentication;

    @Mock
    AuthenticationManager authenticationManager;

    String token = "Bearer 324234324324sfdfsdf434343";
    String habitoId = "abc123";
    String email = "lucas@gmail.com";

    UsuarioDTO dto = UsuarioDTO.builder()
            .email(email)
            .senha("123")
            .build();

    UsuarioEntity entity = UsuarioEntity.builder()
            .email(email)
            .id(321L)
            .nome("Lucas Manoel")
            .senha("123456")
            .build();

    @BeforeEach
    public void setUp() {

        lenient().when(jwtUtil.extrairEmailToken("324234324324sfdfsdf434343")).thenReturn(email);
    }

    @Test
    void deveCadastrarUsuarioComSucesso() {

        UsuarioEntity entity = UsuarioEntity.builder()
                .email(email)
                .senha("senha_criptografada")
                .build();

        UsuarioDTOResponse responseEsperado = new UsuarioDTOResponse("Nome", email);

        when(usuarioRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(dto.getSenha())).thenReturn("senha_criptografada");
        when(usuarioConverter.paraUsuarioEntity(dto)).thenReturn(entity);
        when(usuarioRepository.save(entity)).thenReturn(entity);
        when(usuarioConverter.paraUsuarioDTOResponse(entity)).thenReturn(responseEsperado);

        UsuarioDTOResponse resultado = usuarioService.cadastraUsuario(dto);

        assertNotNull(resultado);
        assertEquals(email, resultado.email());

        verify(usuarioRepository, times(1)).save(entity);
    }

    @Test
    void deveLancarErroQuandoEmailExistir() {
        when(usuarioRepository.existsByEmail(email)).thenReturn(true);
        assertThrows(ConflictException.class, () -> usuarioService.cadastraUsuario(dto));
    }

    @Test
    void deveFazerLoginComSucesso() {
        UsuarioLoginRequest loginRequest = new UsuarioLoginRequest(email, "123");

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getName()).thenReturn(email);
        when(jwtUtil.generateToken(email)).thenReturn("324234324324sfdfsdf434343");

        String resultado = usuarioService.login(loginRequest);

        assertEquals(token, resultado);
    }

    @Test
    void deveBuscarUsuarioPorEmailComSucesso() {
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(entity));
        when(usuarioConverter.paraUsuarioDTOResponse(entity))
                .thenReturn(new UsuarioDTOResponse("Lucas Manoel", email));

        UsuarioDTOResponse resultado = usuarioService.buscaUsuarioPorEmail(token, email);

        assertEquals(email, resultado.email());
    }

    @Test
    void deveDeletarUsuarioPorEmailComSucesso() {
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(entity));

        usuarioService.deletaUsuario(token, email);
        verify(usuarioRepository, times(1)).deleteByEmail(email);
    }

    @Test
    void deveAlterarUsuarioComSenhaComSucesso() {
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(entity));
        when(usuarioConverter.alterarUsuario(dto, entity)).thenReturn(entity);
        when(passwordEncoder.encode(dto.getSenha())).thenReturn("senha_criptografada");
        when(usuarioRepository.save(entity)).thenReturn(entity);

        usuarioService.alteraUsuario(token, dto);

        verify(passwordEncoder, times(1)).encode(dto.getSenha());
        verify(usuarioRepository, times(1)).save(entity);
    }

}
