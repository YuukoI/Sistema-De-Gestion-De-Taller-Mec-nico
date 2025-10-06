package SistemaTallerMecanico.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.aspectj.bridge.IMessage;

import java.time.LocalDate;
import java.util.List;

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

    @ManyToMany
    @JoinTable(
            name = "factura_repuestos",
            joinColumns = @JoinColumn(name = "factura_id"),
            inverseJoinColumns = @JoinColumn(name = "repuesto_id")
    )
    private List<Repuesto> repuestos;

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

    public void calcularPresupuestoTotal(){
        double totalRepuestos = repuestos.stream()
                .mapToDouble(Repuesto::getPrecio)
                .sum();
        this.total = totalRepuestos + manoDeObra;
    }

}
