package medico.cc.repository;

import org.springframework.data.repository.CrudRepository;

import medico.cc.entity.Medico;

public interface MedicoRepository extends CrudRepository<Medico, String>{

}
