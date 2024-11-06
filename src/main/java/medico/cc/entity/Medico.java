package medico.cc.entity;

import java.util.HashSet;
import java.util.Set;

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
    Set<Paciente> pacientes = new HashSet<>() ;

    @OneToMany(mappedBy = "medico", cascade = CascadeType.ALL)
    private Set<Cita> citas;
}
