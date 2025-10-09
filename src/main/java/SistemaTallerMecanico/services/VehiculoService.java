package SistemaTallerMecanico.services;

import SistemaTallerMecanico.entities.Vehiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VehiculoService {

    Page<Vehiculo> getAllVehiculos(Pageable pageable);

    Vehiculo getVehiculoById(Long id);

    Vehiculo save(Vehiculo vehiculo);

    Vehiculo update(Vehiculo vehiculo);

    void deleteById(Long id);

    Page<Vehiculo> findByPatenteContainingIgnoreCaseOrNombrePropietarioContainingIgnoreCase(
            String filtro, Pageable pageable
    );

    Vehiculo findByPatente(String patente);
}
