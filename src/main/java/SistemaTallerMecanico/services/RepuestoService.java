package SistemaTallerMecanico.services;

import SistemaTallerMecanico.entities.Repuesto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RepuestoService {

    Page<Repuesto> findAll(Pageable pageable);

    Repuesto findById(Long id);

    Repuesto save(Repuesto repuesto);

    Repuesto update(Repuesto repuesto);

    void delete(Long id);

    Page<Repuesto> findByNombre(String nombre, Pageable pageable);

    Repuesto actualizarStock(Long id, int nuevoStock);
}
