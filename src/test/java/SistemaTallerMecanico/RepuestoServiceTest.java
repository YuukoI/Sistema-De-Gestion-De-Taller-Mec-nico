package SistemaTallerMecanico;

import SistemaTallerMecanico.entities.Repuesto;
import SistemaTallerMecanico.repositories.RepuestoRepository;
import SistemaTallerMecanico.services.RepuestoServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.*;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
        Page<Repuesto> page = new PageImpl<>(Arrays.asList(repuesto1, repuesto2));
        when(repuestoRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Repuesto> repuestos = repuestoService.findAll(PageRequest.of(0, 10));

        assertEquals(2, repuestos.getContent().size());
        assertEquals("Rueda", repuestos.getContent().get(0).getNombre());
        verify(repuestoRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void testFindById_Found() {
        when(repuestoRepository.findById(1L)).thenReturn(java.util.Optional.of(repuesto1));

        Repuesto repuesto = repuestoService.findById(1L);

        assertNotNull(repuesto);
        assertEquals("Rueda", repuesto.getNombre());
        verify(repuestoRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_NotFound() {
        when(repuestoRepository.findById(anyLong())).thenReturn(java.util.Optional.empty());

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
        Page<Repuesto> page = new PageImpl<>(Arrays.asList(repuesto1));
        when(repuestoRepository.findByNombreContainingIgnoreCase(eq("Rueda"), any(Pageable.class))).thenReturn(page);

        Page<Repuesto> result = repuestoService.findByNombre("Rueda", PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Rueda", result.getContent().get(0).getNombre());
        verify(repuestoRepository, times(1)).findByNombreContainingIgnoreCase(eq("Rueda"), any(Pageable.class));
    }

    @Test
    void testFindByNombre_NotFound() {
        Page<Repuesto> emptyPage = new PageImpl<>(Arrays.asList());
        when(repuestoRepository.findByNombreContainingIgnoreCase(eq("NoExiste"), any(Pageable.class))).thenReturn(emptyPage);

        Page<Repuesto> result = repuestoService.findByNombre("NoExiste", PageRequest.of(0, 10));

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        verify(repuestoRepository, times(1)).findByNombreContainingIgnoreCase(eq("NoExiste"), any(Pageable.class));
    }
}
