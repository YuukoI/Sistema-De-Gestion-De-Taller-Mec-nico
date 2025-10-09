package SistemaTallerMecanico.services;

import SistemaTallerMecanico.entities.Vehiculo;
import SistemaTallerMecanico.repositories.VehiculoRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class VehiculoServiceImp implements VehiculoService {

    private final VehiculoRepository vehiculoRepository;

    @Override
    public Page<Vehiculo> getAllVehiculos(Pageable pageable) {
        return vehiculoRepository.findAll(pageable);
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
    public Page<Vehiculo> findByPatenteContainingIgnoreCaseOrNombrePropietarioContainingIgnoreCase(String filtro, Pageable pageable) {
        return vehiculoRepository.findByPatenteContainingIgnoreCaseOrNombrePropietarioContainingIgnoreCase(filtro, filtro, pageable);
    }

    @Override
    public Vehiculo findByPatente(String patente) {
        return vehiculoRepository.findByPatente(patente);
    }

}
