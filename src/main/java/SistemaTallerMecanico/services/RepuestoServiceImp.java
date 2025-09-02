package SistemaTallerMecanico.services;

import SistemaTallerMecanico.entities.Repuesto;
import SistemaTallerMecanico.repositories.RepuestoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class RepuestoServiceImp implements RepuestoService {

    private final RepuestoRepository repuestoRepository;

    @Override
    public List<Repuesto> findAll() {
        return repuestoRepository.findAll();
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
    public Repuesto findByNombre(String nombre) {
        return repuestoRepository.findByNombre(nombre).orElse(null);
    }
}
