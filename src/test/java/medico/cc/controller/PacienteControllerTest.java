package medico.cc.controller;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import medico.cc.entity.Paciente;
import medico.cc.exception.AlreadyExistInDataBaseException;
import medico.cc.exception.NotFoundInDataBaseException;
import medico.cc.service.PacienteService;
import medico.dto.PacienteDTO;

@WebMvcTest(PacienteController.class)
class PacienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PacienteService pacienteService;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
    }
    
    @Test
    public void testGetPacientes() throws Exception {
        List<Paciente> pacientes = Arrays.asList(new Paciente(), new Paciente());
        
        // Configurar el comportamiento del servicio para devolver una lista simulada de pacientes
        when(pacienteService.getAll()).thenReturn(pacientes);

        mockMvc.perform(get("/paciente/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andDo(print());

        verify(pacienteService, times(1)).getAll();
    }

    @Test
    public void testGetPacienteById_NotFound() throws Exception {
        String pacienteId = "999";

        // Simular que no se encuentra el paciente
        when(pacienteService.getPaciente(pacienteId)).thenReturn(null);

        mockMvc.perform(get("/paciente/" + pacienteId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Paciente no encontrado"))
                .andDo(print());

        verify(pacienteService, times(1)).getPaciente(pacienteId);
    }

    @Test
    public void testAddPaciente_Conflict() throws Exception {
        PacienteDTO pacienteDTO = new PacienteDTO();
        pacienteDTO.setUsuario("user1");  // Usuario que ya existe (causará el conflicto)
        pacienteDTO.setNSS("123456789");
        pacienteDTO.setNumTarjeta("987654321");
        pacienteDTO.setTelefono("987654321");
        pacienteDTO.setDireccion("Direccion paciente");

        // Configurar el mock para simular un conflicto (ya existe el paciente)
        when(pacienteService.convertToEntity(any(PacienteDTO.class))).thenReturn(new Paciente());
        doThrow(new AlreadyExistInDataBaseException("Ya existe un paciente con id user1")).when(pacienteService).postPaciente(any(Paciente.class));

        mockMvc.perform(post("/paciente")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pacienteDTO)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Ya existe un paciente con id user1"))
                .andDo(print());

        verify(pacienteService, times(1)).convertToEntity(any(PacienteDTO.class));
        verify(pacienteService, times(1)).postPaciente(any(Paciente.class));
    }

    @Test
    public void testAddPaciente_Success() throws Exception {
        PacienteDTO pacienteDTO = new PacienteDTO();
        pacienteDTO.setUsuario("user3");
        pacienteDTO.setNSS("123456789");
        pacienteDTO.setNumTarjeta("987654321");
        pacienteDTO.setTelefono("987654321");
        pacienteDTO.setDireccion("Direccion paciente");

        Paciente paciente = new Paciente();
        paciente.setUsuario("user3");
        paciente.setNSS("123456789");
        paciente.setNumTarjeta("987654321");
        paciente.setTelefono("987654321");
        paciente.setDireccion("Direccion paciente");

        when(pacienteService.convertToEntity(any(PacienteDTO.class))).thenReturn(paciente);
        doNothing().when(pacienteService).postPaciente(any(Paciente.class));
        when(pacienteService.convertToDTO(any(Paciente.class))).thenReturn(pacienteDTO);

        mockMvc.perform(post("/paciente")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(pacienteDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.usuario").value("user3"))
                .andExpect(jsonPath("$.nss").value("123456789"))
                .andExpect(jsonPath("$.numTarjeta").value("987654321"))
                .andExpect(jsonPath("$.telefono").value("987654321"))
                .andExpect(jsonPath("$.direccion").value("Direccion paciente"))
                .andDo(print());
    }

    @Test
    public void testPutPaciente_NotFound() throws Exception {
        PacienteDTO pacienteDTO = new PacienteDTO();
        pacienteDTO.setUsuario("user4");
        pacienteDTO.setNSS("987654321");
        pacienteDTO.setNumTarjeta("123456789");
        pacienteDTO.setTelefono("123456789");
        pacienteDTO.setDireccion("Nueva direccion");

        Paciente paciente = new Paciente();
        paciente.setUsuario("user4");
        paciente.setNSS("987654321");
        paciente.setNumTarjeta("123456789");
        paciente.setTelefono("123456789");
        paciente.setDireccion("Nueva direccion");

        when(pacienteService.convertToEntity(any(PacienteDTO.class))).thenReturn(paciente);
        doThrow(new NotFoundInDataBaseException("Paciente no encontrado")).when(pacienteService).putPaciente(any(Paciente.class));

        mockMvc.perform(put("/paciente")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pacienteDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Paciente no encontrado"))
                .andDo(print());

        verify(pacienteService, times(1)).convertToEntity(any(PacienteDTO.class));
        verify(pacienteService, times(1)).putPaciente(any(Paciente.class));
    }

    @Test
    public void testDeletePaciente_Success() throws Exception {
        String pacienteId = "paciente123";

        doNothing().when(pacienteService).deletePaciente(pacienteId);

        mockMvc.perform(delete("/paciente/" + pacienteId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Borrado paciente con id paciente123"))
                .andDo(print());

        verify(pacienteService, times(1)).deletePaciente(pacienteId);
    }

    @Test
    public void testDeletePaciente_NotFound() throws Exception {
        String pacienteId = "paciente123";

        doThrow(new NotFoundInDataBaseException("Paciente no encontrado")).when(pacienteService).deletePaciente(pacienteId);

        mockMvc.perform(delete("/paciente/" + pacienteId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Paciente no encontrado"))
                .andDo(print());

        verify(pacienteService, times(1)).deletePaciente(pacienteId);
    }

    @Test
    public void testRemoveMedicoFromPaciente_Success() throws Exception {
        String pacienteId = "paciente123";
        String medicoId = "medico456";

        Paciente paciente = new Paciente();
        paciente.setUsuario("userPaciente");

        PacienteDTO pacienteDTO = new PacienteDTO();
        pacienteDTO.setUsuario("userPaciente");

        when(pacienteService.removeMedicoFromPaciente(pacienteId, medicoId)).thenReturn(paciente);
        when(pacienteService.convertToDTO(any(Paciente.class))).thenReturn(pacienteDTO);

        mockMvc.perform(delete("/paciente/{paciente_id}/medico/{medico_id}", pacienteId, medicoId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuario").value("userPaciente"))
                .andDo(print());
    }

    @Test
    public void testRemoveMedicoFromPaciente_NotFound() throws Exception {
        String pacienteId = "paciente123";
        String medicoId = "medico456";

        doThrow(new NotFoundInDataBaseException("Paciente o Médico no encontrado")).when(pacienteService).removeMedicoFromPaciente(pacienteId, medicoId);

        mockMvc.perform(delete("/paciente/{paciente_id}/medico/{medico_id}", pacienteId, medicoId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Paciente o Médico no encontrado"))
                .andDo(print());
    }
}
