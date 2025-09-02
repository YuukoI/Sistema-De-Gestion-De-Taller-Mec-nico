package SistemaTallerMecanico;

import SistemaTallerMecanico.controllers.VehiculoController;
import SistemaTallerMecanico.dtos.VehiculoDTO;
import SistemaTallerMecanico.entities.Vehiculo;
import SistemaTallerMecanico.services.VehiculoService;
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

@WebMvcTest(VehiculoController.class)
class VehiculoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VehiculoService vehiculoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Vehiculo vehiculo1;
    private Vehiculo vehiculo2;
    private VehiculoDTO vehiculoDTO;

    @BeforeEach
    void setUp() {
        vehiculo1 = new Vehiculo(1L, "ABC123", "Juan Perez", "Toyota", "Corolla");
        vehiculo2 = new Vehiculo(2L, "DEF456", "Ana Gomez", "Honda", "Civic");

        vehiculoDTO = new VehiculoDTO();
        vehiculoDTO.setPatente("CCC333");
        vehiculoDTO.setNombrePropietario("Carlos Lopez");
        vehiculoDTO.setMarca("Honda");
        vehiculoDTO.setModelo("Civic");
    }

    @Test
    void testFindAll_Success() throws Exception {
        Mockito.when(vehiculoService.getAllVehiculos()).thenReturn(Arrays.asList(vehiculo1, vehiculo2));

        mockMvc.perform(get("/vehiculos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].patente").value("ABC123"))
                .andExpect(jsonPath("$[0].nombrePropietario").value("Juan Perez"));
    }

    @Test
    void testFindAll_NoContent() throws Exception {
        Mockito.when(vehiculoService.getAllVehiculos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/vehiculos"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testFindById_Found() throws Exception {
        Mockito.when(vehiculoService.getVehiculoById(1L)).thenReturn(vehiculo1);

        mockMvc.perform(get("/vehiculos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patente").value("ABC123"))
                .andExpect(jsonPath("$.nombrePropietario").value("Juan Perez"));
    }

    @Test
    void testFindById_NotFound() throws Exception {
        Mockito.when(vehiculoService.getVehiculoById(anyLong())).thenReturn(null);

        mockMvc.perform(get("/vehiculos/10"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSave_Success() throws Exception {
        Vehiculo savedVehiculo = new Vehiculo(3L, vehiculoDTO.getPatente(), vehiculoDTO.getNombrePropietario(), vehiculoDTO.getMarca(), vehiculoDTO.getModelo());
        Mockito.when(vehiculoService.save(any(Vehiculo.class))).thenReturn(savedVehiculo);

        mockMvc.perform(post("/vehiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vehiculoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patente").value("CCC333"))
                .andExpect(jsonPath("$.nombrePropietario").value("Carlos Lopez"));
    }

    @Test
    void testDelete_Success() throws Exception {
        Mockito.when(vehiculoService.getVehiculoById(1L)).thenReturn(vehiculo1);

        mockMvc.perform(delete("/vehiculos/1"))
                .andExpect(status().isOk());

        Mockito.verify(vehiculoService).deleteById(1L);
    }

    @Test
    void testDelete_NotFound() throws Exception {
        Mockito.when(vehiculoService.getVehiculoById(1L)).thenReturn(null);

        mockMvc.perform(delete("/vehiculos/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdate_Success() throws Exception {
        Vehiculo updatedVehiculo = new Vehiculo(1L, vehiculoDTO.getPatente(), vehiculoDTO.getNombrePropietario(), vehiculoDTO.getMarca(), vehiculoDTO.getModelo());
        Mockito.when(vehiculoService.getVehiculoById(1L)).thenReturn(vehiculo1);
        Mockito.when(vehiculoService.save(any(Vehiculo.class))).thenReturn(updatedVehiculo);

        mockMvc.perform(put("/vehiculos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vehiculoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patente").value("CCC333"))
                .andExpect(jsonPath("$.nombrePropietario").value("Carlos Lopez"));
    }

    @Test
    void testUpdate_NotFound() throws Exception {
        Mockito.when(vehiculoService.getVehiculoById(1L)).thenReturn(null);

        mockMvc.perform(put("/vehiculos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vehiculoDTO)))
                .andExpect(status().isNotFound());
    }
}
