package SistemaTallerMecanico.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Repuesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre no puede ser nulo")
    private String nombre;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @NotNull(message = "El stock no puede ser nulo")
    @Positive(message = "El stock debe ser mayor a 0")
    private int stock;

    @NotNull(message = "El precio no puede ser nulo")
    @Positive(message = "El precio debe ser mayor a 0")
    private double precio;
}
