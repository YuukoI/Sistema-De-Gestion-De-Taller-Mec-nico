package SistemaTallerMecanico;

import SistemaTallerMecanico.controllers.PresupuestoController;
import SistemaTallerMecanico.dtos.PresupuestoDTO;
import SistemaTallerMecanico.entities.Presupuesto;
import SistemaTallerMecanico.services.PresupuestoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PresupuestoController.class)
class PresupuestoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PresupuestoService presupuestoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Presupuesto presupuesto1;
    private Presupuesto presupuesto2;
    private PresupuestoDTO presupuestoDTO;

    @BeforeEach
    void setUp() {
        presupuesto1 = new Presupuesto(1L, "ABC123", "Juan Perez", null, 500.0, 1000.0, "Cambio de aceite");
        presupuesto2 = new Presupuesto(2L, "DEF456", "Ana Gomez", null, 300.0, 700.0, "Reparación frenos");

        presupuestoDTO = new PresupuestoDTO();
        presupuestoDTO.setPatente("CCC333");
        presupuestoDTO.setNombrePropietario("Carlos Lopez");
        presupuestoDTO.setRepuestos(null);
        presupuestoDTO.setManoDeObra(200.0);
        presupuestoDTO.setTotal(400.0);
        presupuestoDTO.setDescripcion("Revisión general");
    }

    @Test
    void testFindAll_Success() throws Exception {
        Mockito.when(presupuestoService.findAll()).thenReturn(Arrays.asList(presupuesto1, presupuesto2));

        mockMvc.perform(get("/presupuestos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].patente").value("ABC123"));
    }

    @Test
    void testFindAll_NotFound() throws Exception {
        Mockito.when(presupuestoService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/presupuestos"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testFindById_Found() throws Exception {
        Mockito.when(presupuestoService.findById(1L)).thenReturn(presupuesto1);

        mockMvc.perform(get("/presupuestos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombrePropietario").value("Juan Perez"));
    }

    @Test
    void testFindById_NotFound() throws Exception {
        Mockito.when(presupuestoService.findById(anyLong())).thenReturn(null);

        mockMvc.perform(get("/presupuestos/10"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSave_Success() throws Exception {
        Mockito.when(presupuestoService.save(any(Presupuesto.class))).thenReturn(
                new Presupuesto(3L, presupuestoDTO.getPatente(), presupuestoDTO.getNombrePropietario(),
                        presupuestoDTO.getRepuestos(), presupuestoDTO.getManoDeObra(),
                        presupuestoDTO.getTotal(), presupuestoDTO.getDescripcion())
        );

        mockMvc.perform(post("/presupuestos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(presupuestoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patente").value("CCC333"))
                .andExpect(jsonPath("$.nombrePropietario").value("Carlos Lopez"));
    }

    @Test
    void testDelete_Success() throws Exception {
        Mockito.when(presupuestoService.findById(1L)).thenReturn(presupuesto1);

        mockMvc.perform(delete("/presupuestos/1"))
                .andExpect(status().isOk());

        Mockito.verify(presupuestoService).deleteById(1L);
    }

    @Test
    void testDelete_NotFound() throws Exception {
        Mockito.when(presupuestoService.findById(1L)).thenReturn(null);

        mockMvc.perform(delete("/presupuestos/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdate_Success() throws Exception {
        Presupuesto updated = new Presupuesto(1L, "CCC333", "Carlos Lopez", null, 200.0, 400.0, "Revisión general");

        Mockito.when(presupuestoService.findById(1L)).thenReturn(presupuesto1);
        Mockito.when(presupuestoService.update(any(Presupuesto.class))).thenReturn(updated);

        mockMvc.perform(put("/presupuestos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(presupuestoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombrePropietario").value("Carlos Lopez"));
    }

    @Test
    void testUpdate_NotFound() throws Exception {
        Mockito.when(presupuestoService.findById(1L)).thenReturn(null);

        mockMvc.perform(put("/presupuestos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(presupuestoDTO)))
                .andExpect(status().isNotFound());
    }
}
