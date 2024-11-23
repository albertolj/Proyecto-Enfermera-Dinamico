package medico.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicoDTO {
    private String usuario; // ID del médico
    private String nombre;
    private String apellidos;
    private String clave;
    private String numColegiado;
    private Set<String> pacientes; // IDs o nombres de los pacientes
}
