package medico.cc.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import medico.cc.entity.Medico;
import medico.cc.entity.Paciente;
import medico.cc.exception.AlreadyExistInDataBaseException;
import medico.cc.exception.NotFoundInDataBaseException;
import medico.cc.repository.MedicoRepository;
import medico.dto.MedicoDTO;

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
    if (medicoDB == null) throw new NotFoundInDataBaseException("No se ha encontrado el medico");

    Paciente pacienteDB = pacienteService.getPaciente(paciente_id);
    if (pacienteDB == null) throw new NotFoundInDataBaseException("No se ha encontrado el paciente");

    // Sincronización bidireccional
    medicoDB.addPaciente(pacienteDB);

    // Guardar cambios (usualmente basta guardar uno de los lados)
    medicoRepository.save(medicoDB);

    return medicoDB;
    }

    @Transactional
    public Medico removePacienteFromMedico(String medico_usuario, String paciente_id) throws NotFoundInDataBaseException {
        Medico medicoDB = getMedico(medico_usuario);
    if (medicoDB == null) throw new NotFoundInDataBaseException("No se ha encontrado el medico");

    Paciente pacienteDB = pacienteService.getPaciente(paciente_id);
    if (pacienteDB == null) throw new NotFoundInDataBaseException("No se ha encontrado el paciente");

    // Sincronización bidireccional
    medicoDB.getPacientes().remove(pacienteDB);
    pacienteDB.getMedicos().remove(medicoDB);

    // Guardar cambios
    medicoRepository.save(medicoDB);

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

    public MedicoDTO convertToDTO(Medico medico) {
        MedicoDTO medicoDTO = new MedicoDTO();
        medicoDTO.setUsuario(medico.getUsuario());
        medicoDTO.setNombre(medico.getNombre());
        medicoDTO.setApellidos(medico.getApellidos());
        medicoDTO.setNumColegiado(medico.getNumColegiado());
        medicoDTO.setClave(medico.getClave());

        // Convierte los pacientes en una lista de IDs o nombres
        Set<String> pacientes = medico.getPacientes()
                                      .stream()
                                      .map(Paciente::getUsuario) // O cambiar por otro atributo como nombre
                                      .collect(Collectors.toSet());
        medicoDTO.setPacientes(pacientes);

        return medicoDTO;
    }

    public Medico convertToEntity(MedicoDTO medicoDTO) {
        Medico medico = new Medico();
        medico.setUsuario(medicoDTO.getUsuario());
        medico.setNombre(medicoDTO.getNombre());
        medico.setApellidos(medicoDTO.getApellidos());
        medico.setNumColegiado(medicoDTO.getNumColegiado());
        medico.setClave(medicoDTO.getClave());
        return medico;
    }
}

