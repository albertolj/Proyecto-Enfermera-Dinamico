package medico.cc.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import medico.cc.entity.Usuario;
import medico.cc.exception.AlreadyExistInDataBaseException;
import medico.cc.exception.NotFoundInDataBaseException;
import medico.cc.service.UsuarioService;

@RestController
public class UsuarioController {
    @Autowired
    UsuarioService usuarioService;

    @GetMapping("/usuarios")
    public List<Usuario> getUsuarios(){

        return usuarioService.getAll();
    }


    @GetMapping("/usuario/{usuarios_id}")
    public ResponseEntity<Object> getUsuariosById(@PathVariable(value="usuarios_id") String usuario) {
        if(!usuarioService.usuarioExists(usuario))
            return new ResponseEntity<>("Usuario no encontrado",HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(usuarioService.getUsuario(usuario),HttpStatus.OK);
    }

    @DeleteMapping("/usuario/{id}")
    public ResponseEntity<Object> deleteUsuario(@PathVariable(value="id") String usuario_id) {
        try{
            usuarioService.deleteUsuario(usuario_id);
        }catch (NotFoundInDataBaseException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("Borrado usuario con id " + usuario_id,HttpStatus.OK);
    }

    @PostMapping("/usuario")
    public ResponseEntity<Object> addMedico(@RequestBody Usuario usuario) {

        try {
            usuarioService.postUsuario(usuario);
        } catch (AlreadyExistInDataBaseException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(usuario, HttpStatus.CREATED);
    }
    
    @PutMapping("/usuario")
    public ResponseEntity<Object> putMedico(@RequestBody Usuario usuario) {
        Usuario usuarioDB;
        try {
            usuarioDB = usuarioService.putUsuario(usuario);
        } catch (NotFoundInDataBaseException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(usuarioDB, HttpStatus.OK);
    }
}
