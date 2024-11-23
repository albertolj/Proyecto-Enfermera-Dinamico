package medico.cc.repository;

import org.springframework.data.repository.CrudRepository;

import medico.cc.entity.Paciente;

public interface PacienteRepository extends CrudRepository<Paciente, String>{

}
