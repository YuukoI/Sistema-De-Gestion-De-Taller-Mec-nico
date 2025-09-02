package SistemaTallerMecanico;

import SistemaTallerMecanico.entities.Repuesto;
import SistemaTallerMecanico.repositories.RepuestoRepository;
import SistemaTallerMecanico.services.RepuestoServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class RepuestoServiceTest {

    private RepuestoRepository repuestoRepository;
    private RepuestoServiceImp repuestoService;

    private Repuesto repuesto1;
    private Repuesto repuesto2;

    @BeforeEach
    void setUp() {
        repuestoRepository = Mockito.mock(RepuestoRepository.class);
        repuestoService = new RepuestoServiceImp(repuestoRepository);

        repuesto1 = new Repuesto(1L, "Rueda", "Rueda delantera", 10, 100.0);
        repuesto2 = new Repuesto(2L, "Motor", "Motor 1.6", 5, 500.0);
    }

    @Test
    void testFindAll() {
        when(repuestoRepository.findAll()).thenReturn(Arrays.asList(repuesto1, repuesto2));

        List<Repuesto> repuestos = repuestoService.findAll();

        assertEquals(2, repuestos.size());
        assertEquals("Rueda", repuestos.get(0).getNombre());
        verify(repuestoRepository, times(1)).findAll();
    }

    @Test
    void testFindById_Found() {
        when(repuestoRepository.findById(1L)).thenReturn(Optional.of(repuesto1));

        Repuesto repuesto = repuestoService.findById(1L);

        assertNotNull(repuesto);
        assertEquals("Rueda", repuesto.getNombre());
        verify(repuestoRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_NotFound() {
        when(repuestoRepository.findById(anyLong())).thenReturn(Optional.empty());

        Repuesto repuesto = repuestoService.findById(10L);

        assertNull(repuesto);
        verify(repuestoRepository, times(1)).findById(10L);
    }

    @Test
    void testSave() {
        when(repuestoRepository.save(any(Repuesto.class))).thenReturn(repuesto1);

        Repuesto saved = repuestoService.save(repuesto1);

        assertNotNull(saved);
        assertEquals("Rueda", saved.getNombre());
        verify(repuestoRepository, times(1)).save(repuesto1);
    }

    @Test
    void testUpdate() {
        when(repuestoRepository.save(any(Repuesto.class))).thenReturn(repuesto2);

        Repuesto updated = repuestoService.update(repuesto2);

        assertNotNull(updated);
        assertEquals("Motor", updated.getNombre());
        verify(repuestoRepository, times(1)).save(repuesto2);
    }

    @Test
    void testDelete() {
        doNothing().when(repuestoRepository).deleteById(1L);

        repuestoService.delete(1L);

        verify(repuestoRepository, times(1)).deleteById(1L);
    }

    @Test
    void testFindByNombre_Found() {
        when(repuestoRepository.findByNombre("Rueda")).thenReturn(Optional.of(repuesto1));

        Repuesto repuesto = repuestoService.findByNombre("Rueda");

        assertNotNull(repuesto);
        assertEquals("Rueda", repuesto.getNombre());
        verify(repuestoRepository, times(1)).findByNombre("Rueda");
    }

    @Test
    void testFindByNombre_NotFound() {
        when(repuestoRepository.findByNombre("NoExiste")).thenReturn(Optional.empty());

        Repuesto repuesto = repuestoService.findByNombre("NoExiste");

        assertNull(repuesto);
        verify(repuestoRepository, times(1)).findByNombre("NoExiste");
    }
}
