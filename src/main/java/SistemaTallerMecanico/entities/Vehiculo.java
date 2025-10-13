package SistemaTallerMecanico.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La patente no puede ser nula")
    private String patente;

    @NotBlank(message = "El propietario no puede ser nulo")
    private String nombrePropietario;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String marca;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String modelo;

}
