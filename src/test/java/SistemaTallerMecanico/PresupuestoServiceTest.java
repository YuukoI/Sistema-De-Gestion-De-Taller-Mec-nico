package SistemaTallerMecanico;

import SistemaTallerMecanico.entities.Presupuesto;
import SistemaTallerMecanico.repositories.PresupuestoRepository;
import SistemaTallerMecanico.services.PresupuestoServiceImp;
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

class PresupuestoServiceTest {

    @Mock
    private PresupuestoRepository presupuestoRepository;

    @InjectMocks
    private PresupuestoServiceImp presupuestoService;

    private Presupuesto presupuesto1;
    private Presupuesto presupuesto2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        presupuesto1 = new Presupuesto(1L, "ABC123", "Juan Perez", null, 500.0, 1000.0, "Cambio de aceite");
        presupuesto2 = new Presupuesto(2L, "DEF456", "Ana Gomez", null, 300.0, 700.0, "Reparación frenos");
    }

    @Test
    void testFindAll_Success() {
        when(presupuestoRepository.findAll()).thenReturn(Arrays.asList(presupuesto1, presupuesto2));

        List<Presupuesto> presupuestos = presupuestoService.findAll();

        assertEquals(2, presupuestos.size());
        verify(presupuestoRepository, times(1)).findAll();
    }

    @Test
    void testFindAll_Empty() {
        when(presupuestoRepository.findAll()).thenReturn(Collections.emptyList());

        List<Presupuesto> presupuestos = presupuestoService.findAll();

        assertTrue(presupuestos.isEmpty());
        verify(presupuestoRepository, times(1)).findAll();
    }

    @Test
    void testFindById_Found() {
        when(presupuestoRepository.findById(1L)).thenReturn(java.util.Optional.of(presupuesto1));

        Presupuesto presupuesto = presupuestoService.findById(1L);

        assertNotNull(presupuesto);
        assertEquals("ABC123", presupuesto.getPatente());
        verify(presupuestoRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_NotFound() {
        when(presupuestoRepository.findById(10L)).thenReturn(java.util.Optional.empty());

        Presupuesto presupuesto = presupuestoService.findById(10L);

        assertNull(presupuesto);
        verify(presupuestoRepository, times(1)).findById(10L);
    }

    @Test
    void testSavePresupuesto() {
        when(presupuestoRepository.save(any(Presupuesto.class))).thenReturn(presupuesto1);

        Presupuesto saved = presupuestoService.save(presupuesto1);

        assertNotNull(saved);
        assertEquals("ABC123", saved.getPatente());
        verify(presupuestoRepository, times(1)).save(presupuesto1);
    }

    @Test
    void testUpdatePresupuesto() {
        when(presupuestoRepository.save(any(Presupuesto.class))).thenReturn(presupuesto2);

        Presupuesto updated = presupuestoService.update(presupuesto2);

        assertNotNull(updated);
        assertEquals("DEF456", updated.getPatente());
        verify(presupuestoRepository, times(1)).save(presupuesto2);
    }

    @Test
    void testDeleteById() {
        doNothing().when(presupuestoRepository).deleteById(1L);

        presupuestoService.deleteById(1L);

        verify(presupuestoRepository, times(1)).deleteById(1L);
    }

    @Test
    void testFindPresupuestoByVehiculoPatente_Found() {
        when(presupuestoRepository.findByPatente("ABC123")).thenReturn(Arrays.asList(presupuesto1));

        List<Presupuesto> presupuestos = presupuestoService.findPresupuestoByVehiculoPatente("ABC123");

        assertEquals(1, presupuestos.size());
        assertEquals("ABC123", presupuestos.get(0).getPatente());
        verify(presupuestoRepository, times(1)).findByPatente("ABC123");
    }

    @Test
    void testFindPresupuestoByVehiculoPatente_NotFound() {
        when(presupuestoRepository.findByPatente("NOEXISTE")).thenReturn(Collections.emptyList());

        List<Presupuesto> presupuestos = presupuestoService.findPresupuestoByVehiculoPatente("NOEXISTE");

        assertTrue(presupuestos.isEmpty());
        verify(presupuestoRepository, times(1)).findByPatente("NOEXISTE");
    }

    @Test
    void testFindPresupuestoByVehiculoNombrePropietario_Found() {
        when(presupuestoRepository.findByNombrePropietario("Juan Perez")).thenReturn(Arrays.asList(presupuesto1));

        List<Presupuesto> presupuestos = presupuestoService.findPresupuestoByVehiculoNombrePropietario("Juan Perez");

        assertEquals(1, presupuestos.size());
        assertEquals("Juan Perez", presupuestos.get(0).getNombrePropietario());
        verify(presupuestoRepository, times(1)).findByNombrePropietario("Juan Perez");
    }

    @Test
    void testFindPresupuestoByVehiculoNombrePropietario_NotFound() {
        when(presupuestoRepository.findByNombrePropietario("No Existe")).thenReturn(Collections.emptyList());

        List<Presupuesto> presupuestos = presupuestoService.findPresupuestoByVehiculoNombrePropietario("No Existe");

        assertTrue(presupuestos.isEmpty());
        verify(presupuestoRepository, times(1)).findByNombrePropietario("No Existe");
    }
}
