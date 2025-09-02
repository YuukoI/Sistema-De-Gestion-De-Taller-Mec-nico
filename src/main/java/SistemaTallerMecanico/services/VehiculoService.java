package SistemaTallerMecanico.services;

import SistemaTallerMecanico.entities.Vehiculo;

import java.util.List;

public interface VehiculoService {

    List<Vehiculo> getAllVehiculos();

    Vehiculo getVehiculoById(Long id);

    Vehiculo save(Vehiculo vehiculo);

    Vehiculo update(Vehiculo vehiculo);

    void deleteById(Long id);

    Vehiculo getVehiculoByPatente(String patente);
}
