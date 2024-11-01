package medico.cc.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import medico.cc.entity.Usuario;
import medico.cc.exception.AlreadyExistInDataBaseException;
import medico.cc.exception.NotFoundInDataBaseException;
import medico.cc.repository.UsuarioRepository;

@Service
public class UsuarioService {
    @Autowired
    UsuarioRepository usuarioRepository;

    @Transactional
    public void deleteUsuario(String usuario_id) throws NotFoundInDataBaseException {
        Usuario usuarioDB = getUsuario(usuario_id);
        if (usuarioDB == null) throw new NotFoundInDataBaseException("No se ha encontrado el usuario");
        usuarioRepository.delete(usuarioDB);
    }

    @Transactional
    public void postUsuario(Usuario usuario) throws AlreadyExistInDataBaseException {
        Usuario usuarioDB = getUsuario(usuario.getUsuario());
        if(usuarioDB != null) throw new AlreadyExistInDataBaseException("Ya existe un usuario con id " + usuario.getUsuario());
        usuarioRepository.save(usuario);
    }

    public Usuario getUsuario(String usuario_id) {
        return usuarioRepository.findById(usuario_id).orElse(null);
    }

    public List<Usuario> getAll(){
        return new ArrayList<>((List<Usuario>) usuarioRepository.findAll());
    }


    public boolean usuarioExists(String usuario) {
        return getUsuario(usuario) != null;
    }

    @Transactional
    public Usuario putUsuario(Usuario usuario) throws NotFoundInDataBaseException {
        Usuario usuarioDB = getUsuario(usuario.getUsuario());
        if(usuarioDB == null) throw new NotFoundInDataBaseException("No se ha encontrado el usuario");
        usuarioDB.setNombre(usuario.getNombre());
        usuarioDB.setApellidos(usuario.getApellidos());
        usuarioDB.setClave(usuario.getClave());

        usuarioRepository.save(usuarioDB);
        return usuarioDB;
    }
}
