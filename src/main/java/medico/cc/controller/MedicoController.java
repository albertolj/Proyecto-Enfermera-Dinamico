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

import medico.cc.entity.Medico;
import medico.cc.exception.AlreadyExistInDataBaseException;
import medico.cc.exception.NotFoundInDataBaseException;
import medico.cc.service.MedicoService;
import medico.dto.MedicoDTO;

@RestController
@RequestMapping("/medico")
public class MedicoController {
    @Autowired
    MedicoService medicoService;

    @GetMapping("/all")
    public ResponseEntity<List<MedicoDTO>> getAllMedicos() {
        List<MedicoDTO> medicos = medicoService.getAll()
            .stream()
            .map(medicoService::convertToDTO)
            .collect(Collectors.toList());
        return new ResponseEntity<>(medicos, HttpStatus.OK);
    }

    @GetMapping("/{medico_id}")
    public ResponseEntity<Object> getMedicoById(@PathVariable(value = "medico_id") String medico_id) {
        Medico medico = medicoService.getMedico(medico_id);
        if (medico == null) {
            return new ResponseEntity<>("Medico no encontrado", HttpStatus.NOT_FOUND);
        }
        MedicoDTO medicoDTO = medicoService.convertToDTO(medico);
        return new ResponseEntity<>(medicoDTO, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Object> addMedico(@RequestBody MedicoDTO medicoDTO) {
        Medico medico = medicoService.convertToEntity(medicoDTO);
        try {
            medicoService.postMedico(medico);
        } catch (AlreadyExistInDataBaseException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(medicoService.convertToDTO(medico), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Object> putMedico(@RequestBody MedicoDTO medicoDTO) {
        Medico medico = medicoService.convertToEntity(medicoDTO);
        try {
            Medico updatedMedico = medicoService.putMedico(medico);
            return new ResponseEntity<>(medicoService.convertToDTO(updatedMedico), HttpStatus.OK);
        } catch (NotFoundInDataBaseException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping
    public ResponseEntity<Object> patchMedico(@RequestBody MedicoDTO medicoDTO) {
        Medico medico = medicoService.convertToEntity(medicoDTO);
        try {
            Medico updatedMedico = medicoService.patchMedico(medico);
            return new ResponseEntity<>(medicoService.convertToDTO(updatedMedico), HttpStatus.OK);
        } catch (NotFoundInDataBaseException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{medico_id}/paciente/{paciente_id}")
    public ResponseEntity<Object> addPacienteToMedico(
            @PathVariable(value = "medico_id") String medico_id,
            @PathVariable(value = "paciente_id") String paciente_id) {
        try {
            Medico updatedMedico = medicoService.addPacienteToMedico(medico_id, paciente_id);
            return new ResponseEntity<>(medicoService.convertToDTO(updatedMedico), HttpStatus.OK);
        } catch (NotFoundInDataBaseException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteMedico(@PathVariable(value = "id") String medico_id) {
        try {
            medicoService.deleteMedico(medico_id);
            return new ResponseEntity<>("Borrado medico con id " + medico_id, HttpStatus.OK);
        } catch (NotFoundInDataBaseException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{medico_id}/paciente/{paciente_id}")
    public ResponseEntity<Object> removePacienteFromMedico(
            @PathVariable(value = "medico_id") String medico_id,
            @PathVariable(value = "paciente_id") String paciente_id) {
        try {
            Medico updatedMedico = medicoService.removePacienteFromMedico(medico_id, paciente_id);
            return new ResponseEntity<>(medicoService.convertToDTO(updatedMedico), HttpStatus.OK);
        } catch (NotFoundInDataBaseException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

}
