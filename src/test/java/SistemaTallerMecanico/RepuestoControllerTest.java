package SistemaTallerMecanico;

import SistemaTallerMecanico.controllers.RepuestoController;
import SistemaTallerMecanico.entities.Repuesto;
import SistemaTallerMecanico.services.RepuestoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class RepuestoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RepuestoService repuestoService;

    @InjectMocks
    private RepuestoController repuestoController;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Repuesto repuesto1;
    private Repuesto repuesto2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(repuestoController).build();

        repuesto1 = new Repuesto(1L, "Rueda", "Rueda de automóvil", 10, 100.0);
        repuesto2 = new Repuesto(2L, "Motor", "Motor 2.0L", 5, 500.0);
    }

    @Test
    void testFindAllPaged_Success() throws Exception {
        Page<Repuesto> page = new PageImpl<>(Arrays.asList(repuesto1, repuesto2));
        Mockito.when(repuestoService.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/repuestos?page=0&size=10&sortBy=id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].nombre").value("Rueda"))
                .andExpect(jsonPath("$.content[1].nombre").value("Motor"));
    }

    @Test
    void testFindAllPaged_NotFound() throws Exception {
        Page<Repuesto> emptyPage = new PageImpl<>(Collections.emptyList());
        Mockito.when(repuestoService.findAll(any(Pageable.class))).thenReturn(emptyPage);

        mockMvc.perform(get("/repuestos?page=0&size=10&sortBy=id"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSearchByNombre_Found() throws Exception {
        Page<Repuesto> page = new PageImpl<>(Arrays.asList(repuesto1));
        Mockito.when(repuestoService.findByNombre(eq("Rueda"), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/repuestos/search?nombre=Rueda&page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].nombre").value("Rueda"));
    }

    @Test
    void testSearchByNombre_NotFound() throws Exception {
        Page<Repuesto> emptyPage = new PageImpl<>(Collections.emptyList());
        Mockito.when(repuestoService.findByNombre(eq("NoExiste"), any(Pageable.class))).thenReturn(emptyPage);

        mockMvc.perform(get("/repuestos/search?nombre=NoExiste&page=0&size=10"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSearch_NoNombre_ReturnsAll() throws Exception {
        Page<Repuesto> page = new PageImpl<>(Arrays.asList(repuesto1, repuesto2));
        Mockito.when(repuestoService.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/repuestos/search?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    void testFindById_Found() throws Exception {
        Mockito.when(repuestoService.findById(1L)).thenReturn(repuesto1);

        mockMvc.perform(get("/repuestos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Rueda"))
                .andExpect(jsonPath("$.descripcion").value("Rueda de automóvil"))
                .andExpect(jsonPath("$.stock").value(10))
                .andExpect(jsonPath("$.precio").value(100.0));
    }

    @Test
    void testFindById_NotFound() throws Exception {
        Mockito.when(repuestoService.findById(any(Long.class))).thenReturn(null);

        mockMvc.perform(get("/repuestos/10"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSave_Success() throws Exception {
        Mockito.when(repuestoService.save(any(Repuesto.class))).thenReturn(repuesto1);

        mockMvc.perform(post("/repuestos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(repuesto1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Rueda"))
                .andExpect(jsonPath("$.descripcion").value("Rueda de automóvil"))
                .andExpect(jsonPath("$.stock").value(10))
                .andExpect(jsonPath("$.precio").value(100.0));
    }

    @Test
    void testDelete_Success() throws Exception {
        Mockito.when(repuestoService.findById(1L)).thenReturn(repuesto1);

        mockMvc.perform(delete("/repuestos/1"))
                .andExpect(status().isOk());

        Mockito.verify(repuestoService).delete(1L);
    }

    @Test
    void testDelete_NotFound() throws Exception {
        Mockito.when(repuestoService.findById(1L)).thenReturn(null);

        mockMvc.perform(delete("/repuestos/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdate_Success() throws Exception {
        Repuesto repuestoActualizado = new Repuesto(1L, "Rueda Actualizada", "Rueda nueva", 12, 120.0);
        Mockito.when(repuestoService.findById(1L)).thenReturn(repuesto1);
        Mockito.when(repuestoService.save(any(Repuesto.class))).thenReturn(repuestoActualizado);

        mockMvc.perform(put("/repuestos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(repuestoActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Rueda Actualizada"))
                .andExpect(jsonPath("$.descripcion").value("Rueda nueva"))
                .andExpect(jsonPath("$.stock").value(12))
                .andExpect(jsonPath("$.precio").value(120.0));
    }

    @Test
    void testUpdate_NotFound() throws Exception {
        Mockito.when(repuestoService.findById(1L)).thenReturn(null);

        mockMvc.perform(put("/repuestos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(repuesto1)))
                .andExpect(status().isNotFound());
    }
}
