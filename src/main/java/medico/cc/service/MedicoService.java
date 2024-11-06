package medico.cc.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import medico.cc.entity.Medico;
import medico.cc.entity.Paciente;
import medico.cc.exception.AlreadyExistInDataBaseException;
import medico.cc.exception.NotFoundInDataBaseException;
import medico.cc.repository.MedicoRepository;

@Service
public class MedicoService {

    @Autowired
    MedicoRepository medicoRepository;
    @Autowired
    @Lazy
    PacienteService pacienteService;

    public boolean medicoExists(String usuario){
        Medico medicoDB = getMedico(usuario);
        return medicoDB != null;
    }

    @Transactional
    public void postMedico(Medico medico) throws AlreadyExistInDataBaseException {
        Medico medicoDB = getMedico(medico.getUsuario());
        if(medicoDB != null) throw new AlreadyExistInDataBaseException("Ya existe una cita con id " + medico.getUsuario());
        medicoRepository.save(medico);
    }


    public Medico getMedico(String usuario){

        return medicoRepository.findById(usuario).orElse(null);
    }

    @Transactional
    public Medico putMedico(Medico medico) throws NotFoundInDataBaseException {
        Medico medicoDB = getMedico(medico.getUsuario());
        if(medicoDB == null) throw new NotFoundInDataBaseException("No se ha encontrado el medico");
        medicoDB.setNombre(medico.getNombre());
        medicoDB.setApellidos(medico.getApellidos());
        medicoDB.setClave(medico.getClave());
        medicoDB.setNumColegiado(medico.getNumColegiado());

        medicoRepository.save(medicoDB);
        return medicoDB;
    }
    @Transactional
    public Medico patchMedico(Medico medico) throws NotFoundInDataBaseException {
        Medico medicoDB = getMedico(medico.getUsuario());
        if(medicoDB == null) throw new NotFoundInDataBaseException("No se ha encontrado el medico");
        if(medico.getNombre()!= null)           medicoDB.setNombre(medico.getNombre());
        if(medico.getApellidos()!= null)      medicoDB.setApellidos(medico.getApellidos());
        if(!medico.getUsuario().isEmpty())    medicoDB.setUsuario(medico.getUsuario());
        if(medico.getNumColegiado()!= null)   medicoDB.setNumColegiado(medico.getNumColegiado());

        medicoRepository.save(medicoDB);
        return medicoDB;
    }

    @Transactional
    public void deleteMedico(String usuario) throws NotFoundInDataBaseException {
        Medico medicoDB = getMedico(usuario);
        if(medicoDB == null) throw new NotFoundInDataBaseException("No se ha encontrado el medico");
        medicoRepository.delete(medicoDB);

    }
    
    @Transactional
    public Medico addPacienteToMedico(String medico_id, String paciente_id) throws NotFoundInDataBaseException {
        Medico medicoDB = getMedico(medico_id);
        if(medicoDB == null) throw new NotFoundInDataBaseException("No se ha encontrado el medico");
        Paciente pacienteDB = pacienteService.getPaciente(paciente_id);
        if(pacienteDB == null) throw new NotFoundInDataBaseException("No se ha encontrado el paciente");
        pacienteDB.getMedicos().add(medicoDB);
        medicoDB.getPacientes().add(pacienteDB);
        return medicoDB;
    }

    @Transactional
    public Medico removePacienteFromMedico(String medico_usuario, String paciente_id) throws NotFoundInDataBaseException {
        Medico medicoDB = getMedico(medico_usuario);
        if(medicoDB == null) throw new NotFoundInDataBaseException("No se ha encontrado el medico");
        Paciente pacienteDB = pacienteService.getPaciente(paciente_id);
        if(pacienteDB == null) throw new NotFoundInDataBaseException("No se ha encontrado el paciente");
        pacienteDB.getMedicos().remove(medicoDB);
        medicoDB.getPacientes().remove(pacienteDB);
        return medicoDB;
    }

    public List<Medico> getAll(){
        return new ArrayList<>((List<Medico>) medicoRepository.findAll());
    }

    public List<Paciente> getAllPacientes(String medico_usuario) throws NotFoundInDataBaseException {
        Medico medicoDB = getMedico(medico_usuario);
        if(medicoDB == null) throw new NotFoundInDataBaseException("No se ha encontrado el medico");
        return new ArrayList<>(medicoDB.getPacientes());
    }
}
