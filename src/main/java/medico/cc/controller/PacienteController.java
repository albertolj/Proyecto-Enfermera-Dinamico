package medico.cc.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;

import medico.cc.entity.Paciente;
import medico.cc.exception.AlreadyExistInDataBaseException;
import medico.cc.exception.NotFoundInDataBaseException;
import medico.cc.service.PacienteService;

@RestController
public class PacienteController {
    @Autowired
    PacienteService pacienteService;

    @GetMapping("/pacientes")
    public List<Paciente> getPacientes(){

        return pacienteService.getAll();
        //return pacienteMapper.toPacienteDTOlist(pacienteService.getAll());
    }

    @GetMapping("/paciente/{paciente_id}")
    public ResponseEntity<Object> getPacientesById(@PathVariable(value="paciente_id") String paciente) {
        if(!pacienteService.pacienteExists(paciente))
            return new ResponseEntity<>("Paciente no encontrado",HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(pacienteService.getPaciente(paciente),HttpStatus.OK);
    }

    @PostMapping("/paciente")
    public ResponseEntity<Object> addPaciente(@RequestBody Paciente paciente) {

        try {
            pacienteService.postPaciente(paciente);
        } catch (AlreadyExistInDataBaseException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(paciente, HttpStatus.CREATED);
    }

    @PutMapping("/paciente")
    public ResponseEntity<Object> putPaciente(@RequestBody Paciente paciente) {
        Paciente pacienteDB;
        try {
            pacienteDB = pacienteService.putPaciente(paciente);
        } catch (NotFoundInDataBaseException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(pacienteDB, HttpStatus.OK);
    }

    @PatchMapping("/paciente")
    public ResponseEntity<Object> patchPaciente(@RequestBody Paciente paciente) {
        Paciente pacienteDB;
        try {
            pacienteDB = pacienteService.patchPaciente(paciente);
        } catch (NotFoundInDataBaseException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(pacienteDB, HttpStatus.OK);
    }

    @DeleteMapping("/paciente/{id}")
    public ResponseEntity<Object> deletePaciente(@PathVariable(value="id") String paciente_id) {
        try{
            pacienteService.deletePaciente(paciente_id);
        }catch (NotFoundInDataBaseException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("Borrado paciente con id " + paciente_id,HttpStatus.OK);
    }

    @DeleteMapping("/paciente/{paciente_id}/medico/{medico_id}")
    public ResponseEntity<Object> removeMedicoFromPaciente(
            @PathVariable(value="paciente_id") String paciente_id,
            @PathVariable(value="medico_id") String medico_id) {
        Paciente pacienteDB;
        try{
            pacienteDB = pacienteService.removeMedicoFromPaciente(paciente_id,medico_id);
        }catch (NotFoundInDataBaseException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(pacienteDB,HttpStatus.OK);
    }
}
