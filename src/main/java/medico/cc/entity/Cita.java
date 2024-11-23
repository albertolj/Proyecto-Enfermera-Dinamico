package medico.cc.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name= "cita")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer atribute11;
    @JsonFormat(pattern = "E MMM dd HH:mm:ss z yyyy", timezone = "CET")
    private LocalDateTime fechaHora;
    private String motivoCita;

    public Cita(LocalDateTime fechaHora, String motivoCita, Integer atribute11) {
        this.fechaHora = fechaHora;
        this.motivoCita = motivoCita;
        this.atribute11 = atribute11;
    }

    public Cita(Integer atribute11, LocalDateTime fechaHora, String motivoCita, Medico medico, Paciente paciente) {
        this.fechaHora = fechaHora;
        this.motivoCita = motivoCita;
        this.atribute11 = atribute11;
        this.paciente = paciente;
        this.medico = medico;

    }

    @ManyToOne
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;
    //Corregir

    @ManyToOne
    @JoinColumn(name = "medico_id")
    private Medico medico;

}

