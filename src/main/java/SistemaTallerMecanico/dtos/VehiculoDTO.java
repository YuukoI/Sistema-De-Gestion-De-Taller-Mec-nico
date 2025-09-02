package SistemaTallerMecanico.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehiculoDTO {

    @NotBlank(message = "La patente no puede ser nula")
    private String patente;

    @NotBlank(message = "El propietario no puede ser nulo")
    private String nombrePropietario;

    private String marca;

    private String modelo;

}
