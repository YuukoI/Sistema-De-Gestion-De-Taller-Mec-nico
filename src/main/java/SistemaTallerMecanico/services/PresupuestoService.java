package SistemaTallerMecanico.services;

import SistemaTallerMecanico.entities.Presupuesto;

import java.util.List;

public interface PresupuestoService {

    List<Presupuesto> findAll();

    Presupuesto findById(long id);

    Presupuesto save(Presupuesto presupuesto);

    Presupuesto update(Presupuesto presupuesto);

    void deleteById(long id);

    List<Presupuesto> findPresupuestoByVehiculoPatente(String patente);

    List<Presupuesto> findPresupuestoByVehiculoNombrePropietario(String nombrePropietario);
}
