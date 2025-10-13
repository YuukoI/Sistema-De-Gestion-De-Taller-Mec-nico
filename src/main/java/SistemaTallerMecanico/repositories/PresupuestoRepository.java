package SistemaTallerMecanico.repositories;

import SistemaTallerMecanico.entities.Presupuesto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PresupuestoRepository extends JpaRepository<Presupuesto, Long> {

    List<Presupuesto> findByPatente(String patente);

    List<Presupuesto> findByNombrePropietario(String nombrePropietario);

    Page<Presupuesto> findByPatenteContainingIgnoreCaseOrNombrePropietarioContainingIgnoreCase(String patente, String nombrePropietario, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Presupuesto p WHERE p.fecha BETWEEN :inicio AND :fin")
    long contarPorFecha(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    @Query("SELECT COALESCE(SUM(p.total), 0) FROM Presupuesto p WHERE p.fecha BETWEEN :inicio AND :fin")
    Double sumarTotalPorFecha(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);
}
