package SistemaTallerMecanico.services;

import SistemaTallerMecanico.entities.Repuesto;

import java.util.List;

public interface RepuestoService {

    List<Repuesto> findAll();

    Repuesto findById(Long id);

    Repuesto save(Repuesto repuesto);

    Repuesto update(Repuesto repuesto);

    void delete(Long id);

    Repuesto findByNombre(String nombre);
}
