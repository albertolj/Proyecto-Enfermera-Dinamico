package medico.cc.controller;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import medico.cc.entity.Usuario;
import medico.cc.service.UsuarioService;

@WebMvcTest(UsuarioController.class)
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Usuario usuario1;
    private Usuario usuario2;

    @BeforeEach
    void setUp() {
        // Crear instancias de Usuario con los atributos correspondientes
        usuario1 = new Usuario();
        usuario1.setUsuario("user1");
        usuario1.setNombre("Juan");
        usuario1.setApellidos("Perez");
        usuario1.setClave("clave123");

        usuario2 = new Usuario();
        usuario2.setUsuario("user2");
        usuario2.setNombre("Maria");
        usuario2.setApellidos("Lopez");
        usuario2.setClave("clave456");
    }

    @Test
    public void testGetUsuarios() throws Exception {
        List<Usuario> usuarios = Arrays.asList(usuario1, usuario2);

        when(usuarioService.getAll()).thenReturn(usuarios);

        mockMvc.perform(get("/usuarios")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].usuario").value("user1"))
                .andExpect(jsonPath("$[1].usuario").value("user2"))
                .andDo(print());

        verify(usuarioService, times(1)).getAll();
    }

    @Test
    public void testGetUsuarioById_NotFound() throws Exception {
        String usuarioId = "999";
        when(usuarioService.usuarioExists(usuarioId)).thenReturn(false);

        mockMvc.perform(get("/usuario/" + usuarioId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Usuario no encontrado"))
                .andDo(print());

        verify(usuarioService, times(1)).usuarioExists(usuarioId);
    }

    @Test
    public void testAddUsuario_Success() throws Exception {
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsuario("user3");
        nuevoUsuario.setNombre("Carlos");
        nuevoUsuario.setApellidos("Sanchez");
        nuevoUsuario.setClave("clave789");

        doNothing().when(usuarioService).postUsuario(any(Usuario.class));

        mockMvc.perform(post("/usuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevoUsuario)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.usuario").value("user3"))
                .andExpect(jsonPath("$.nombre").value("Carlos"))
                .andExpect(jsonPath("$.apellidos").value("Sanchez"))
                .andDo(print());

        verify(usuarioService, times(1)).postUsuario(any(Usuario.class));
    }
}
