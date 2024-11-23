package medico.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PacienteDTO {
    private String usuario; // ID del paciente
    private String NSS;
    private String numTarjeta;
    private String telefono;
    private String direccion;
}
