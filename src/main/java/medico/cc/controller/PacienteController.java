package medico.cc.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import medico.cc.entity.Paciente;
import medico.cc.exception.AlreadyExistInDataBaseException;
import medico.cc.exception.NotFoundInDataBaseException;
import medico.cc.service.PacienteService;
import medico.dto.PacienteDTO;

@RestController
@RequestMapping("/paciente")
public class PacienteController {
    @Autowired
    PacienteService pacienteService;

    @GetMapping("/all")
    public ResponseEntity<List<PacienteDTO>> getAllPacientes() {
        List<PacienteDTO> pacientes = pacienteService.getAll()
            .stream()
            .map(pacienteService::convertToDTO)
            .collect(Collectors.toList());
        return new ResponseEntity<>(pacientes, HttpStatus.OK);
    }

    @GetMapping("/{paciente_id}")
    public ResponseEntity<Object> getPacienteById(@PathVariable(value = "paciente_id") String paciente_id) {
        Paciente paciente = pacienteService.getPaciente(paciente_id);
        if (paciente == null) {
            return new ResponseEntity<>("Paciente no encontrado", HttpStatus.NOT_FOUND);
        }
        PacienteDTO pacienteDTO = pacienteService.convertToDTO(paciente);
        return new ResponseEntity<>(pacienteDTO, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Object> addPaciente(@RequestBody PacienteDTO pacienteDTO) {
        Paciente paciente = pacienteService.convertToEntity(pacienteDTO);
        try {
            pacienteService.postPaciente(paciente);
        } catch (AlreadyExistInDataBaseException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(pacienteService.convertToDTO(paciente), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Object> putPaciente(@RequestBody PacienteDTO pacienteDTO) {
        Paciente paciente = pacienteService.convertToEntity(pacienteDTO);
        try {
            Paciente updatedPaciente = pacienteService.putPaciente(paciente);
            return new ResponseEntity<>(pacienteService.convertToDTO(updatedPaciente), HttpStatus.OK);
        } catch (NotFoundInDataBaseException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping
    public ResponseEntity<Object> patchPaciente(@RequestBody PacienteDTO pacienteDTO) {
        Paciente paciente = pacienteService.convertToEntity(pacienteDTO);
        try {
            Paciente updatedPaciente = pacienteService.patchPaciente(paciente);
            return new ResponseEntity<>(pacienteService.convertToDTO(updatedPaciente), HttpStatus.OK);
        } catch (NotFoundInDataBaseException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletePaciente(@PathVariable(value = "id") String paciente_id) {
        try {
            pacienteService.deletePaciente(paciente_id);
            return new ResponseEntity<>("Borrado paciente con id " + paciente_id, HttpStatus.OK);
        } catch (NotFoundInDataBaseException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{paciente_id}/medico/{medico_id}")
    public ResponseEntity<Object> removeMedicoFromPaciente(
            @PathVariable(value = "paciente_id") String paciente_id,
            @PathVariable(value = "medico_id") String medico_id) {
        try {
            Paciente updatedPaciente = pacienteService.removeMedicoFromPaciente(paciente_id, medico_id);
            return new ResponseEntity<>(pacienteService.convertToDTO(updatedPaciente), HttpStatus.OK);
        } catch (NotFoundInDataBaseException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
