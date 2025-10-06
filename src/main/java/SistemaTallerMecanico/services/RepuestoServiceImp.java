package SistemaTallerMecanico.services;

import SistemaTallerMecanico.entities.Repuesto;
import SistemaTallerMecanico.repositories.RepuestoRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class RepuestoServiceImp implements RepuestoService {

    private final RepuestoRepository repuestoRepository;

    @Override
    public Page<Repuesto> findAll(Pageable pageable) {
        return repuestoRepository.findAll(pageable);
    }

    @Override
    public Repuesto findById(Long id) {
        return repuestoRepository.findById(id).orElse(null);
    }

    @Override
    public Repuesto save(Repuesto repuesto) {
        return repuestoRepository.save(repuesto);
    }

    @Override
    public Repuesto update(Repuesto repuesto) {
        return repuestoRepository.save(repuesto);
    }

    @Override
    public void delete(Long id) {
        repuestoRepository.deleteById(id);
    }

    @Override
    public Page<Repuesto> findByNombre(String nombre, Pageable pageable) {
        return repuestoRepository.findByNombreContainingIgnoreCase(nombre, pageable);
    }

    @Override
    public Repuesto actualizarStock(Long id, int nuevoStock) {
        Repuesto r = repuestoRepository.findById(id).orElseThrow(() -> new RuntimeException("Repuesto no encontrado"));
        r.setStock(nuevoStock);
        return repuestoRepository.save(r);
    }
}
