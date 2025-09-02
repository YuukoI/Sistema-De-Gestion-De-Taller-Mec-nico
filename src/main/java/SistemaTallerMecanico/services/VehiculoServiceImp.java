package SistemaTallerMecanico.services;

import SistemaTallerMecanico.entities.Vehiculo;
import SistemaTallerMecanico.repositories.VehiculoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class VehiculoServiceImp implements VehiculoService {

    private final VehiculoRepository vehiculoRepository;

    @Override
    public List<Vehiculo> getAllVehiculos() {
        return vehiculoRepository.findAll();
    }

    @Override
    public Vehiculo getVehiculoById(Long id) {
        return vehiculoRepository.findById(id).orElse(null);
    }

    @Override
    public Vehiculo save(Vehiculo vehiculo) {
        return vehiculoRepository.save(vehiculo);
    }

    @Override
    public Vehiculo update(Vehiculo vehiculo) {
        return vehiculoRepository.save(vehiculo);
    }

    @Override
    public void deleteById(Long id) {
        vehiculoRepository.deleteById(id);
    }

    @Override
    public Vehiculo getVehiculoByPatente(String patente) {
        return vehiculoRepository.findByPatente(patente).orElse(null);
    }
}
