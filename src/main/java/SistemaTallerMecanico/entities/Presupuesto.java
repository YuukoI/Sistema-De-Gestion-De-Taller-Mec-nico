package SistemaTallerMecanico.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Presupuesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Debe ingresar una patente")
    private String patente;

    @NotBlank(message = "Debe ingresar el nombre del propietario del vehiculo")
    private String nombrePropietario;

    @NotNull(message = "Mano de obra no puede ser nulo")
    @Positive(message = "Mano de obra no puede ser negativo")
    private double manoDeObra;

    @NotNull(message = "Precio total no puede ser nulo")
    @Positive(message = "El precio total debe ser positivo")
    private Double total;

    @NotBlank(message = "Falta descripción")
    private String descripcion;

    private LocalDate fecha;

    private String marca;

    private String modelo;

}
