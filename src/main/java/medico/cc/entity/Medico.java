package medico.cc.entity;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name= "medico")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Medico extends Usuario{
    private String numColegiado;

    @ManyToMany(fetch = FetchType.EAGER,cascade = { CascadeType.ALL })
    @JoinTable(
            name = "medico_paciente",
            joinColumns = { @JoinColumn(name = "medico_id") },
            inverseJoinColumns = { @JoinColumn(name = "paciente_id") })
    @JsonManagedReference
    Set<Paciente> pacientes = new HashSet<>() ;

    public Medico(String nombre, String apellidos, String usuario, String clave, String numcolegiado){
        this.setNombre(nombre);
        this.setApellidos(apellidos);
        this.setUsuario(usuario);
        this.setClave(clave);
        this.setNumColegiado(numcolegiado);
    }

    @OneToMany(mappedBy = "medico", cascade = CascadeType.ALL)
    private Set<Cita> citas;

    public void addPaciente(Paciente paciente) {
        this.pacientes.add(paciente);
        paciente.getMedicos().add(this); // Sincroniza el lado inverso
    }
}

