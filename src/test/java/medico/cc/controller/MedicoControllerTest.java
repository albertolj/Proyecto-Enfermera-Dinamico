package medico.cc.controller;

import java.util.Arrays;
import java.util.Collections;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import medico.cc.entity.Medico;
import medico.cc.entity.Paciente;
import medico.cc.exception.AlreadyExistInDataBaseException;
import medico.cc.exception.NotFoundInDataBaseException;
import medico.cc.service.MedicoService;
import medico.dto.MedicoDTO;

@WebMvcTest(MedicoController.class)
class MedicoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MedicoService medicoService;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
    }
    
    @Test
    public void testGetMedicos() throws Exception {
        // Crear un listado simulado de MedicoDTO para el test
        List<Medico> medicos = Arrays.asList(new Medico(), new Medico());
    
        // Configurar el comportamiento del servicio para que devuelva el listado simulado
        when(medicoService.getAll()).thenReturn(medicos);

        // Realizar la petición GET a la URL correcta: /medico/all
        mockMvc.perform(get("/medico/all") // Aquí la ruta debe ser "/medico/all"
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Esperamos un 200 OK
                .andExpect(jsonPath("$.size()").value(2)) // Comprobamos que hay 2 elementos
                .andDo(print());

        // Verificar que el servicio getAll fue llamado una vez
        verify(medicoService, times(1)).getAll();
    }

    @Test
    public void testGetMedicoById_NotFound() throws Exception {
        String medicoId = "999";
        
        // Mockeamos el servicio para que getMedico devuelva null, indicando que no se encontró el médico
        when(medicoService.getMedico(medicoId)).thenReturn(null);

        mockMvc.perform(get("/medico/" + medicoId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Medico no encontrado"))
                .andDo(print());

        // Verifica que se haya invocado el método getMedico
        verify(medicoService, times(1)).getMedico(medicoId);
    }

    @Test
    public void testAddMedico_Conflict() throws Exception {
        // Crear un MedicoDTO que se usará para el test
        MedicoDTO medicoDTO = new MedicoDTO();
        medicoDTO.setUsuario("user1");  // Usuario que ya existe (causará el conflicto)
        medicoDTO.setNombre("Juan");
        medicoDTO.setApellidos("Perez");
        medicoDTO.setClave("clave123");
        medicoDTO.setNumColegiado("77");

        // Configurar el mock para simular un conflicto (ya existe el médico)
        when(medicoService.convertToEntity(any(MedicoDTO.class))).thenReturn(new Medico());
        doThrow(new AlreadyExistInDataBaseException("Ya existe una cita con id user1")).when(medicoService).postMedico(any(Medico.class));

        // Realizar la llamada al POST y verificar el estado 409 con el mensaje esperado
        mockMvc.perform(post("/medico")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(medicoDTO))) // Convertir el MedicoDTO a JSON
                .andExpect(status().isConflict())  // Verificar que devuelve un 409
                .andExpect(content().string("Ya existe una cita con id user1"))  // Verificar el mensaje de error
                .andDo(print());  // Imprimir el resultado para depuración

        // Verificar que se intentó convertir el DTO y luego lanzar la excepción
        verify(medicoService, times(1)).convertToEntity(any(MedicoDTO.class));
        verify(medicoService, times(1)).postMedico(any(Medico.class));
    }


    @Test
    public void testAddMedico_Success() throws Exception {
        // Crear un MedicoDTO de ejemplo
        MedicoDTO medicoDTO = new MedicoDTO();
        medicoDTO.setUsuario("user3");
        medicoDTO.setNombre("Carlos");
        medicoDTO.setApellidos("Sanchez");
        medicoDTO.setNumColegiado("123");
        medicoDTO.setClave("clave789");

        // Crear el Medico correspondiente que se retornará
        Medico medico = new Medico();
        medico.setUsuario("user3");
        medico.setNombre("Carlos");
        medico.setApellidos("Sanchez");
        medico.setNumColegiado("123");
        medico.setClave("clave789");

        // Simula la conversión de DTO a entidad
        when(medicoService.convertToEntity(any(MedicoDTO.class))).thenReturn(medico);
        // Simula la acción de agregar el medico (en el servicio)
        doNothing().when(medicoService).postMedico(any(Medico.class));

        // Simula la conversión de entidad a DTO (respuesta final)
        when(medicoService.convertToDTO(any(Medico.class))).thenReturn(medicoDTO);

        // Realizar la petición POST y verificar la respuesta
        mockMvc.perform(post("/medico")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(medicoDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.usuario").value("user3"))
                .andExpect(jsonPath("$.nombre").value("Carlos"))
                .andExpect(jsonPath("$.apellidos").value("Sanchez"))
                .andExpect(jsonPath("$.numColegiado").value("123"))
                .andExpect(jsonPath("$.clave").value("clave789"))
                .andDo(print());  // Imprimir la respuesta para depuración
    }

    @Test
    public void testAddPacienteToMedico_Success() throws Exception {
        String medicoId = "medico123";
        String pacienteId = "paciente456";

        // Crear un MedicoDTO de ejemplo
        MedicoDTO medicoDTO = new MedicoDTO();
        medicoDTO.setUsuario("user3");
        medicoDTO.setNombre("Carlos");
        medicoDTO.setApellidos("Sanchez");
        medicoDTO.setNumColegiado("123");
        medicoDTO.setClave("clave789");

        // Crear un Paciente con un ID para agregar
        Paciente paciente = new Paciente();
        paciente.setUsuario("userPaciente");
        paciente.setNSS("123456789");
        paciente.setNumTarjeta("987654321");
        paciente.setTelefono("987654321");
        paciente.setDireccion("Direccion paciente");

        // Crear el Medico correspondiente que se retornará
        Medico medico = new Medico();
        medico.setUsuario("user3");
        medico.setNombre("Carlos");
        medico.setApellidos("Sanchez");
        medico.setNumColegiado("123");
        medico.setClave("clave789");

        // Agregar el paciente al set de pacientes del Medico (trabajamos con el objeto Paciente)
        medico.addPaciente(paciente);  // Usamos el método addPaciente para agregar el paciente

        // Crear el DTO final que se va a retornar
        medicoDTO.setPacientes(Collections.singleton("userPaciente"));  // Asignamos el ID del paciente en el DTO

        // Simula la actualización del Medico con el paciente agregado
        when(medicoService.addPacienteToMedico(medicoId, pacienteId)).thenReturn(medico);
        // Simula la conversión de entidad a DTO (respuesta final)
        when(medicoService.convertToDTO(any(Medico.class))).thenReturn(medicoDTO);

        // Realizar la petición PATCH y verificar la respuesta
        mockMvc.perform(patch("/medico/{medico_id}/paciente/{paciente_id}", medicoId, pacienteId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuario").value("user3"))
                .andExpect(jsonPath("$.nombre").value("Carlos"))
                .andExpect(jsonPath("$.apellidos").value("Sanchez"))
                .andExpect(jsonPath("$.numColegiado").value("123"))
                .andExpect(jsonPath("$.clave").value("clave789"))
                .andExpect(jsonPath("$.pacientes").isArray())
                .andExpect(jsonPath("$.pacientes[0]").value("userPaciente"))  // Verificamos el ID del paciente
                .andDo(print());  // Imprimir la respuesta para depuración
    }

    @Test
    public void testAddPacienteToMedico_NotFound() throws Exception {
        String medicoId = "medico123";
        String pacienteId = "paciente456";

        // Simula que no se encuentra al Medico o al Paciente
        when(medicoService.addPacienteToMedico(medicoId, pacienteId))
                .thenThrow(new NotFoundInDataBaseException("Medico o Paciente no encontrado"));

        // Realizar la petición PATCH y verificar que el error se maneje correctamente
        mockMvc.perform(patch("/medico/{medico_id}/paciente/{paciente_id}", medicoId, pacienteId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Medico o Paciente no encontrado"))
                .andDo(print());  // Imprimir la respuesta para depuración
    }

    @Test
    public void testRemovePacienteFromMedico_Success() throws Exception {
        String medicoId = "medico123";
        String pacienteId = "paciente456";

        // Crear un MedicoDTO de ejemplo
        MedicoDTO medicoDTO = new MedicoDTO();
        medicoDTO.setUsuario("user3");
        medicoDTO.setNombre("Carlos");
        medicoDTO.setApellidos("Sanchez");
        medicoDTO.setNumColegiado("123");
        medicoDTO.setClave("clave789");

        // Crear un Paciente con un ID para agregar
        Paciente paciente = new Paciente();
        paciente.setUsuario("userPaciente");
        paciente.setNSS("123456789");
        paciente.setNumTarjeta("987654321");
        paciente.setTelefono("987654321");
        paciente.setDireccion("Direccion paciente");

        // Crear el Medico correspondiente que se retornará
        Medico medico = new Medico();
        medico.setUsuario("user3");
        medico.setNombre("Carlos");
        medico.setApellidos("Sanchez");
        medico.setNumColegiado("123");
        medico.setClave("clave789");

        // Agregar el paciente al set de pacientes del Medico (trabajamos con el objeto Paciente)
        medico.addPaciente(paciente);  // Usamos el método addPaciente para agregar el paciente

        // Eliminar el paciente del set de pacientes del Medico
        medico.getPacientes().remove(paciente);  // Aquí simulamos la eliminación del paciente

        // Crear el DTO final que se va a retornar (el paciente ya no está en el Set)
        medicoDTO.setPacientes(Collections.emptySet());  // Ahora el Set de pacientes está vacío

        // Simula la eliminación del paciente en el médico
        when(medicoService.removePacienteFromMedico(medicoId, pacienteId)).thenReturn(medico);
        // Simula la conversión de entidad a DTO (respuesta final)
        when(medicoService.convertToDTO(any(Medico.class))).thenReturn(medicoDTO);

        // Realizar la petición DELETE y verificar la respuesta
        mockMvc.perform(delete("/medico/{medico_id}/paciente/{paciente_id}", medicoId, pacienteId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuario").value("user3"))
                .andExpect(jsonPath("$.nombre").value("Carlos"))
                .andExpect(jsonPath("$.apellidos").value("Sanchez"))
                .andExpect(jsonPath("$.numColegiado").value("123"))
                .andExpect(jsonPath("$.clave").value("clave789"))
                .andExpect(jsonPath("$.pacientes").isEmpty())  // Verificamos que el set de pacientes está vacío
                .andDo(print());  // Imprimir la respuesta para depuración
    }

    @Test
    public void testRemovePacienteFromMedico_NotFound() throws Exception {
        String medicoId = "medico123";
        String pacienteId = "paciente456";

        // Simular que el médico no se encuentra en la base de datos
        when(medicoService.removePacienteFromMedico(medicoId, pacienteId))
            .thenThrow(new NotFoundInDataBaseException("No se ha encontrado el medico"));

        // Realizar la petición DELETE y verificar la respuesta
        mockMvc.perform(delete("/medico/{medico_id}/paciente/{paciente_id}", medicoId, pacienteId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())  // Verificamos que el código de estado es 404
                .andExpect(content().string("No se ha encontrado el medico"))  // Verificamos que el mensaje de error es correcto
                .andDo(print());  // Imprimir la respuesta para depuración
    }
}