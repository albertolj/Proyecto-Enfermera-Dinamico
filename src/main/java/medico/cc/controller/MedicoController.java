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

import medico.cc.entity.Medico;
import medico.cc.exception.AlreadyExistInDataBaseException;
import medico.cc.exception.NotFoundInDataBaseException;
import medico.cc.service.MedicoService;

@RestController
public class MedicoController {
    @Autowired
    MedicoService medicoService;

    @GetMapping("/medicos")
    public List<Medico> getUsuarios(){

        return medicoService.getAll();
    }


    @GetMapping("/medico/{medicos_id}")
    public ResponseEntity<Object> getMedicosById(@PathVariable(value="medicos_id") String usuario) {
        if(!medicoService.medicoExists(usuario))
            return new ResponseEntity<>("Medico no encontrado",HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(medicoService.getMedico(usuario),HttpStatus.OK);
    }


    @PostMapping("/medico")
    public ResponseEntity<Object> addMedico(@RequestBody Medico medico) {

        try {
            medicoService.postMedico(medico);
        } catch (AlreadyExistInDataBaseException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(medico, HttpStatus.CREATED);
    }

    @PutMapping("/medico")
    public ResponseEntity<Object> putMedico(@RequestBody Medico medico) {
        Medico medicoDB;
        try {
            medicoDB = medicoService.putMedico(medico);
        } catch (NotFoundInDataBaseException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(medicoDB, HttpStatus.OK);
    }

    @PatchMapping("/medico")
    public ResponseEntity<Object> patchMedico(@RequestBody Medico medico) {
        Medico medicoDB;
        try {
            medicoDB = medicoService.patchMedico(medico);
        } catch (NotFoundInDataBaseException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(medicoDB, HttpStatus.OK);
    }

    @PatchMapping("/medico/{medico_id}/paciente/{paciente_id}")
    public ResponseEntity<Object> addMedicoToMedico(
            @PathVariable(value="medico_id") String medico_id,
            @PathVariable(value="paciente_id") String paciente_id) {
        Medico medicoDB;
        try{
            medicoDB = medicoService.addPacienteToMedico(medico_id,paciente_id);
        }catch (NotFoundInDataBaseException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(medicoDB,HttpStatus.OK);
    }

    @DeleteMapping("/medico/{id}")
    public ResponseEntity<Object> deleteMedico(@PathVariable(value="id") String medico_id) {
        try{
            medicoService.deleteMedico(medico_id);
        }catch (NotFoundInDataBaseException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("Borrado medico con id " + medico_id,HttpStatus.OK);
    }


    @DeleteMapping("/medico/{medico_id}/paciente/{paciente_id}")
    public ResponseEntity<Object> removeMedicoFromMedico(
            @PathVariable(value="medico_id") String medico_id,
            @PathVariable(value="paciente_id") String paciente_id) {
        Medico medicoDB;
        try{
            medicoDB = medicoService.removePacienteFromMedico(medico_id,paciente_id);
        }catch (NotFoundInDataBaseException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(medicoDB,HttpStatus.OK);
    }
}
