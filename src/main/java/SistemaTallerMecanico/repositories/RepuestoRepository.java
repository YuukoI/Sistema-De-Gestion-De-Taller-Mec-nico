package SistemaTallerMecanico.repositories;

import SistemaTallerMecanico.entities.Repuesto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Repository
public interface RepuestoRepository extends JpaRepository<Repuesto, Long> {

    Page<Repuesto> findAll(Pageable pageable);

    Page<Repuesto> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

}
