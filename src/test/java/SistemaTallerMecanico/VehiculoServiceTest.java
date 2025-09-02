package SistemaTallerMecanico;

import SistemaTallerMecanico.entities.Vehiculo;
import SistemaTallerMecanico.repositories.VehiculoRepository;
import SistemaTallerMecanico.services.VehiculoServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VehiculoServiceTest {

    @Mock
    private VehiculoRepository vehiculoRepository;

    @InjectMocks
    private VehiculoServiceImp vehiculoService;

    private Vehiculo vehiculo1;
    private Vehiculo vehiculo2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        vehiculo1 = new Vehiculo(1L, "ABC123", "Juan Perez", "Toyota", "Corolla");
        vehiculo2 = new Vehiculo(2L, "XYZ987", "Analia Garcia", "Ford", "Fiesta");
    }

    @Test
    void testGetAllVehiculos_Success() {
        when(vehiculoRepository.findAll()).thenReturn(Arrays.asList(vehiculo1, vehiculo2));

        List<Vehiculo> vehiculos = vehiculoService.getAllVehiculos();

        assertEquals(2, vehiculos.size());
        verify(vehiculoRepository, times(1)).findAll();
    }

    @Test
    void testGetAllVehiculos_Empty() {
        when(vehiculoRepository.findAll()).thenReturn(Collections.emptyList());

        List<Vehiculo> vehiculos = vehiculoService.getAllVehiculos();

        assertTrue(vehiculos.isEmpty());
        verify(vehiculoRepository, times(1)).findAll();
    }

    @Test
    void testGetVehiculoById_Found() {
        when(vehiculoRepository.findById(1L)).thenReturn(java.util.Optional.of(vehiculo1));

        Vehiculo vehiculo = vehiculoService.getVehiculoById(1L);

        assertNotNull(vehiculo);
        assertEquals("ABC123", vehiculo.getPatente());
        assertEquals("Juan Perez", vehiculo.getNombrePropietario());
        verify(vehiculoRepository, times(1)).findById(1L);
    }

    @Test
    void testGetVehiculoById_NotFound() {
        when(vehiculoRepository.findById(10L)).thenReturn(java.util.Optional.empty());

        Vehiculo vehiculo = vehiculoService.getVehiculoById(10L);

        assertNull(vehiculo);
        verify(vehiculoRepository, times(1)).findById(10L);
    }

    @Test
    void testSaveVehiculo() {
        when(vehiculoRepository.save(any(Vehiculo.class))).thenReturn(vehiculo1);

        Vehiculo saved = vehiculoService.save(vehiculo1);

        assertNotNull(saved);
        assertEquals("ABC123", saved.getPatente());
        assertEquals("Juan Perez", saved.getNombrePropietario());
        verify(vehiculoRepository, times(1)).save(vehiculo1);
    }

    @Test
    void testUpdateVehiculo() {
        when(vehiculoRepository.save(any(Vehiculo.class))).thenReturn(vehiculo2);

        Vehiculo updated = vehiculoService.update(vehiculo2);

        assertNotNull(updated);
        assertEquals("XYZ987", updated.getPatente());
        assertEquals("Analia Garcia", updated.getNombrePropietario());
        verify(vehiculoRepository, times(1)).save(vehiculo2);
    }

    @Test
    void testDeleteById() {
        doNothing().when(vehiculoRepository).deleteById(1L);

        vehiculoService.deleteById(1L);

        verify(vehiculoRepository, times(1)).deleteById(1L);
    }

    @Test
    void testGetVehiculoByPatente_Found() {
        when(vehiculoRepository.findByPatente("ABC123")).thenReturn(java.util.Optional.of(vehiculo1));

        Vehiculo vehiculo = vehiculoService.getVehiculoByPatente("ABC123");

        assertNotNull(vehiculo);
        assertEquals("ABC123", vehiculo.getPatente());
        assertEquals("Juan Perez", vehiculo.getNombrePropietario());
        verify(vehiculoRepository, times(1)).findByPatente("ABC123");
    }

    @Test
    void testGetVehiculoByPatente_NotFound() {
        when(vehiculoRepository.findByPatente("NOEXISTE")).thenReturn(java.util.Optional.empty());

        Vehiculo vehiculo = vehiculoService.getVehiculoByPatente("NOEXISTE");

        assertNull(vehiculo);
        verify(vehiculoRepository, times(1)).findByPatente("NOEXISTE");
    }
}
