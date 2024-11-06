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
import medico.cc.repository.PacienteRepository;

@Service
public class PacienteService {

    @Autowired
    @Lazy
    MedicoService medicoService;

    @Autowired
    PacienteRepository pacienteRepository;

    public boolean pacienteExists(String usuario) {
        Paciente pacienteDB = getPaciente(usuario);
        return pacienteDB != null;
    }

    @Transactional
    public void postPaciente(Paciente paciente) throws AlreadyExistInDataBaseException {
        Paciente pacienteDB = getPaciente(paciente.getUsuario());
        if (pacienteDB != null) throw new AlreadyExistInDataBaseException("Ya existe un paciente con id " + paciente.getUsuario());
        pacienteRepository.save(paciente);
    }

    public Paciente getPaciente(String usuario) {
        return pacienteRepository.findById(usuario).orElse(null);
    }

    @Transactional
    public Paciente putPaciente(Paciente paciente) throws NotFoundInDataBaseException {
        Paciente pacienteDB = getPaciente(paciente.getUsuario());
        if (pacienteDB == null) throw new NotFoundInDataBaseException("No se ha encontrado el paciente");
        pacienteDB.setNombre(paciente.getNombre());
        pacienteDB.setApellidos(paciente.getApellidos());
        pacienteDB.setClave(paciente.getClave());
        pacienteDB.setNSS(paciente.getNSS());
        pacienteDB.setNumTarjeta(paciente.getNumTarjeta());
        pacienteDB.setTelefono(paciente.getTelefono());
        pacienteDB.setDireccion(paciente.getDireccion());

        pacienteRepository.save(pacienteDB);
        return pacienteDB;
    }

    @Transactional
    public Paciente patchPaciente(Paciente paciente) throws NotFoundInDataBaseException {
        Paciente pacienteDB = getPaciente(paciente.getUsuario());
        if (pacienteDB == null) throw new NotFoundInDataBaseException("No se ha encontrado el paciente");
        if (paciente.getNombre() != null) pacienteDB.setNombre(paciente.getNombre());
        if (paciente.getApellidos() != null) pacienteDB.setApellidos(paciente.getApellidos());
        if (paciente.getNSS() != null) pacienteDB.setNSS(paciente.getNSS());
        if (paciente.getNumTarjeta() != null) pacienteDB.setNumTarjeta(paciente.getNumTarjeta());
        if (paciente.getTelefono() != null) pacienteDB.setTelefono(paciente.getTelefono());
        if (paciente.getDireccion() != null) pacienteDB.setDireccion(paciente.getDireccion());

        pacienteRepository.save(pacienteDB);
        return pacienteDB;
    }


    @Transactional
    public void deletePaciente(String usuario) throws NotFoundInDataBaseException {
        Paciente pacienteDB = getPaciente(usuario);
        if (pacienteDB == null) throw new NotFoundInDataBaseException("No se ha encontrado el usuario");
        pacienteRepository.delete(pacienteDB);
    }

    //Relaciones
    @Transactional
    public Paciente addMedicoToPaciente(String paciente_id, String medico_id) throws NotFoundInDataBaseException {
        Paciente pacienteDB = getPaciente(paciente_id);
        if (pacienteDB == null) throw new NotFoundInDataBaseException("No se ha encontrado el paciente");
        Medico medicoDB = medicoService.getMedico(medico_id);
        if (medicoDB == null) throw new NotFoundInDataBaseException("No se ha encontrado el medico");
        pacienteDB.getMedicos().add(medicoDB);
        medicoDB.getPacientes().add(pacienteDB);
        return pacienteDB;
    }

    @Transactional
    public Paciente removeMedicoFromPaciente(String paciente_id, String medico_id) throws NotFoundInDataBaseException {
        Paciente pacienteDB = getPaciente(paciente_id);
        if (pacienteDB == null) throw new NotFoundInDataBaseException("No se ha encontrado el paciente");
        Medico medicoDB = medicoService.getMedico(medico_id);
        if (medicoDB == null) throw new NotFoundInDataBaseException("No se ha encontrado el medico");
        pacienteDB.getMedicos().remove(medicoDB);
        medicoDB.getPacientes().remove(pacienteDB);
        return pacienteDB;
    }

    //Get All

    public List<Paciente> getAll(){
        return new ArrayList<>((List<Paciente>) pacienteRepository.findAll());
    }

    public List<Medico> getAllMedicos(String pacienteUsuario) throws NotFoundInDataBaseException {
        Paciente pacienteDB = getPaciente(pacienteUsuario);
        if (pacienteDB == null) throw new NotFoundInDataBaseException("No se ha encontrado el paciente");
        return new ArrayList<>(pacienteDB.getMedicos());
    }
}
