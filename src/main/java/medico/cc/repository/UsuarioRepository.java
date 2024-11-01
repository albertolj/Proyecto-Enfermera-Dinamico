package medico.cc.repository;

import org.springframework.data.repository.CrudRepository;

import medico.cc.entity.Usuario;

public interface UsuarioRepository extends CrudRepository<Usuario, String>{
    
}
