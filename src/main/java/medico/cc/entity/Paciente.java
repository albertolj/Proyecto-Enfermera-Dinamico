package medico.cc.entity;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@NoArgsConstructor
@Entity
@Table(name = "paciente")
public class Paciente extends Usuario{
    private String NSS;
    private String numTarjeta;
    private String telefono;
    private String direccion;

    @ManyToMany(mappedBy = "pacientes", fetch = FetchType.EAGER,cascade = { CascadeType.ALL })
    @JsonIgnore
    Set<Medico> medicos = new HashSet<>();

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL)
    private Set<Cita> citas;

    public void addMedico(Medico medico) {
        this.medicos.add(medico);
        medico.getPacientes().add(this); // Sincroniza el lado inverso
    }
}

