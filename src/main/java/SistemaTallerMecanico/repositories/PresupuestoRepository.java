package SistemaTallerMecanico.repositories;

import SistemaTallerMecanico.entities.Presupuesto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PresupuestoRepository extends JpaRepository<Presupuesto, Long> {

    List<Presupuesto> findByPatente(String patente);

    List<Presupuesto> findByNombrePropietario(String nombrePropietario);

    Page<Presupuesto> findByPatenteContainingIgnoreCaseOrNombrePropietarioContainingIgnoreCase(String patente, String nombrePropietario, Pageable pageable);
}
