package SistemaTallerMecanico.repositories;

import SistemaTallerMecanico.entities.Vehiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    Page<Vehiculo> findByPatenteContainingIgnoreCaseOrNombrePropietarioContainingIgnoreCase(
            String patente, String nombrePropietario, Pageable pageable
    );

    Page<Vehiculo> findAll(Pageable pageable);

    Vehiculo findByPatente(String patente);

}
