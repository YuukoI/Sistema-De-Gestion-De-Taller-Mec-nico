package SistemaTallerMecanico;


import SistemaTallerMecanico.entities.Vehiculo;
import SistemaTallerMecanico.repositories.VehiculoRepository;
import SistemaTallerMecanico.services.VehiculoServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VehiculoServiceTest {

    @InjectMocks
    private VehiculoServiceImp vehiculoService;

    @Mock
    private VehiculoRepository vehiculoRepository;

    private Vehiculo vehiculo1;
    private Vehiculo vehiculo2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        vehiculo1 = new Vehiculo();
        vehiculo1.setId(1L);
        vehiculo1.setPatente("ABC123");
        vehiculo1.setMarca("Toyota");
        vehiculo1.setModelo("Corolla");
        vehiculo1.setNombrePropietario("Juan Perez");

        vehiculo2 = new Vehiculo();
        vehiculo2.setId(2L);
        vehiculo2.setPatente("XYZ789");
        vehiculo2.setMarca("Ford");
        vehiculo2.setModelo("Focus");
        vehiculo2.setNombrePropietario("Maria Lopez");
    }

    @Test
    void testGetAllVehiculos() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Vehiculo> page = new PageImpl<>(Arrays.asList(vehiculo1, vehiculo2));

        when(vehiculoRepository.findAll(pageable)).thenReturn(page);

        Page<Vehiculo> result = vehiculoService.getAllVehiculos(pageable);

        assertEquals(2, result.getContent().size());
        verify(vehiculoRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetVehiculoById_Found() {
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo1));

        Vehiculo result = vehiculoService.getVehiculoById(1L);

        assertNotNull(result);
        assertEquals("ABC123", result.getPatente());
        verify(vehiculoRepository, times(1)).findById(1L);
    }

    @Test
    void testGetVehiculoById_NotFound() {
        when(vehiculoRepository.findById(3L)).thenReturn(Optional.empty());

        Vehiculo result = vehiculoService.getVehiculoById(3L);

        assertNull(result);
        verify(vehiculoRepository, times(1)).findById(3L);
    }

    @Test
    void testSave() {
        when(vehiculoRepository.save(vehiculo1)).thenReturn(vehiculo1);

        Vehiculo result = vehiculoService.save(vehiculo1);

        assertNotNull(result);
        assertEquals("ABC123", result.getPatente());
        verify(vehiculoRepository, times(1)).save(vehiculo1);
    }

    @Test
    void testUpdate() {
        when(vehiculoRepository.save(vehiculo2)).thenReturn(vehiculo2);

        Vehiculo result = vehiculoService.update(vehiculo2);

        assertNotNull(result);
        assertEquals("XYZ789", result.getPatente());
        verify(vehiculoRepository, times(1)).save(vehiculo2);
    }

    @Test
    void testDeleteById() {
        doNothing().when(vehiculoRepository).deleteById(1L);

        vehiculoService.deleteById(1L);

        verify(vehiculoRepository, times(1)).deleteById(1L);
    }

    @Test
    void testFindByPatenteContainingIgnoreCaseOrNombrePropietarioContainingIgnoreCase() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Vehiculo> page = new PageImpl<>(List.of(vehiculo1));

        when(vehiculoRepository.findByPatenteContainingIgnoreCaseOrNombrePropietarioContainingIgnoreCase("Juan", "Juan", pageable))
                .thenReturn(page);

        Page<Vehiculo> result = vehiculoService.findByPatenteContainingIgnoreCaseOrNombrePropietarioContainingIgnoreCase("Juan", pageable);

        assertEquals(1, result.getContent().size());
        assertEquals("Juan Perez", result.getContent().get(0).getNombrePropietario());
        verify(vehiculoRepository, times(1))
                .findByPatenteContainingIgnoreCaseOrNombrePropietarioContainingIgnoreCase("Juan", "Juan", pageable);
    }
}
