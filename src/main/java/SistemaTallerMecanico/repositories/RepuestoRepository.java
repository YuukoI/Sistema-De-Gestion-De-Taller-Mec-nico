package SistemaTallerMecanico.repositories;

import SistemaTallerMecanico.entities.Repuesto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepuestoRepository extends JpaRepository<Repuesto, Long> {
    Optional<Repuesto> findByNombre(String nombre);
}
