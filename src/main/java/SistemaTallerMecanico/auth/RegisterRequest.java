package SistemaTallerMecanico.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank
    @Size(max = 25, message = "El username no puede superar 25 caracteres")
    private String username;


    @Size(max = 25, message = "El apellido no puede superar 25 caracteres")
    private String lastname;

    @Size(max = 25, message = "El nombre no puede superar 25 caracteres")
    private String firstname;

    @Size(max = 25, message = "El país no puede superar 25 caracteres")
    private String country;

    @NotBlank
    @Size(max = 30, message = "La contraseña no puede superar los 30 caracteres")
    private String password;

}