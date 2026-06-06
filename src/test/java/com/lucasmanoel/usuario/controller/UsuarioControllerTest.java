package com.lucasmanoel.usuario.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lucasmanoel.usuario.business.UsuarioService;
import com.lucasmanoel.usuario.business.dto.UsuarioDTO;
import com.lucasmanoel.usuario.business.dto.UsuarioDTOResponse;
import com.lucasmanoel.usuario.business.dto.UsuarioLoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @InjectMocks
    UsuarioController usuarioController;

    @Mock
    UsuarioService usuarioService;

    MockMvc mockMvc;

    ObjectMapper objectMapper;

    final String TOKEN = "Bearer token_de_teste_123";
    final String HABITO_ID = "abc123";
    String email = "lucas@gmail.com";

    UsuarioDTO dto = UsuarioDTO.builder()
            .email(email)
            .senha("123")
            .build();
    UsuarioDTOResponse dtoResponse = UsuarioDTOResponse.builder()
            .email(email)
            .build();

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders
                .standaloneSetup(usuarioController)
                .build();
    }

    @Test
    void deveCadastrarUsuarioComSucessoERetornar201() throws Exception {
        when(usuarioService.cadastraUsuario(dto)).thenReturn(dtoResponse);

        mockMvc.perform(post("/usuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(email));
        verify(usuarioService).cadastraUsuario(dto);
    }

    @Test
    void deveAlterarUsuarioComSucessoERetornar200() throws Exception {
        when(usuarioService.alteraUsuario(TOKEN, dto)).thenReturn(dtoResponse);

        mockMvc.perform(put("/usuario")
                        .header("Authorization", TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));

        verify(usuarioService).alteraUsuario(TOKEN, dto);
    }

    @Test
    void deveBuscarUsuarioPorEmailComSucessoERetornar200() throws Exception {
        when(usuarioService.buscaUsuarioPorEmail(TOKEN, email)).thenReturn(dtoResponse);

        mockMvc.perform(get("/usuario")
                        .header("Authorization", TOKEN)
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));

        verify(usuarioService).buscaUsuarioPorEmail(TOKEN, email);
    }

    @Test
    void deveFazerLoginComSucessoERetornar200() throws Exception {
        UsuarioLoginRequest loginRequest = UsuarioLoginRequest.builder()
                .email(email)
                .senha(dto.getSenha())
                .build();

        when(usuarioService.login(loginRequest)).thenReturn(TOKEN);

        mockMvc.perform(post("/usuario/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(TOKEN));

        verify(usuarioService).login(loginRequest);
    }

    @Test
    void deveDeletarUsuarioPorEmailComSucessoERetornar204() throws Exception {

        mockMvc.perform(delete("/usuario/{email}", email)
                        .header("Authorization", TOKEN))
                .andExpect(status().isNoContent());

        verify(usuarioService).deletaUsuario(TOKEN, email);
    }
}
