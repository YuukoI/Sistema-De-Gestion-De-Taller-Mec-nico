package SistemaTallerMecanico.services;

import SistemaTallerMecanico.entities.Presupuesto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PresupuestoService {

    Page<Presupuesto> findAll(Pageable pageable);

    Presupuesto findById(long id);

    Presupuesto save(Presupuesto presupuesto);

    Presupuesto update(Presupuesto presupuesto);

    void deleteById(long id);

    List<Presupuesto> findPresupuestoByVehiculoPatente(String patente);

    List<Presupuesto> findPresupuestoByVehiculoNombrePropietario(String nombrePropietario);

    Page<Presupuesto> findByPatenteContainingIgnoreCaseOrNombrePropietarioContainingIgnoreCase(String filtro, Pageable pageable);
}
