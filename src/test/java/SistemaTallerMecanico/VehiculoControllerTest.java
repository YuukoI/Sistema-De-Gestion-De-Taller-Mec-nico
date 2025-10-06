package SistemaTallerMecanico;


import SistemaTallerMecanico.controllers.VehiculoController;
import SistemaTallerMecanico.dtos.VehiculoDTO;
import SistemaTallerMecanico.entities.Vehiculo;
import SistemaTallerMecanico.services.VehiculoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.mockito.Mockito.*;

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

    @BeforeEach
    void setUp() {
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
    @WithMockUser(roles = {"USER"})
    void testFindAllPaged() throws Exception {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
        Page<Vehiculo> page = new PageImpl<>(Arrays.asList(vehiculo1, vehiculo2));

        when(vehiculoService.getAllVehiculos(pageable)).thenReturn(page);

        mockMvc.perform(get("/vehiculos")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].patente").value("ABC123"));

        verify(vehiculoService, times(1)).getAllVehiculos(pageable);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testFindById_Found() throws Exception {
        when(vehiculoService.getVehiculoById(1L)).thenReturn(vehiculo1);

        mockMvc.perform(get("/vehiculos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patente").value("ABC123"));

        verify(vehiculoService, times(1)).getVehiculoById(1L);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testFindById_NotFound() throws Exception {
        when(vehiculoService.getVehiculoById(3L)).thenReturn(null);

        mockMvc.perform(get("/vehiculos/3"))
                .andExpect(status().isNotFound());

        verify(vehiculoService, times(1)).getVehiculoById(3L);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testSave() throws Exception {
        VehiculoDTO dto = new VehiculoDTO();
        dto.setPatente("NEW123");
        dto.setMarca("Honda");
        dto.setModelo("Civic");
        dto.setNombrePropietario("Pedro Gomez");

        Vehiculo vehiculoGuardado = new Vehiculo();
        vehiculoGuardado.setId(3L);
        vehiculoGuardado.setPatente(dto.getPatente());
        vehiculoGuardado.setMarca(dto.getMarca());
        vehiculoGuardado.setModelo(dto.getModelo());
        vehiculoGuardado.setNombrePropietario(dto.getNombrePropietario());

        when(vehiculoService.save(any(Vehiculo.class))).thenReturn(vehiculoGuardado);

        mockMvc.perform(post("/vehiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.patente").value("NEW123"));

        verify(vehiculoService, times(1)).save(any(Vehiculo.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testUpdate_Found() throws Exception {
        VehiculoDTO dto = new VehiculoDTO();
        dto.setPatente("UPDATED123");
        dto.setMarca("Honda");
        dto.setModelo("Civic");
        dto.setNombrePropietario("Pedro Gomez");

        when(vehiculoService.getVehiculoById(1L)).thenReturn(vehiculo1);
        when(vehiculoService.save(any(Vehiculo.class))).thenReturn(vehiculo1);

        mockMvc.perform(put("/vehiculos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(vehiculoService, times(1)).getVehiculoById(1L);
        verify(vehiculoService, times(1)).save(any(Vehiculo.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testUpdate_NotFound() throws Exception {
        VehiculoDTO dto = new VehiculoDTO();
        dto.setPatente("UPDATED123");
        dto.setMarca("Honda");
        dto.setModelo("Civic");
        dto.setNombrePropietario("Pedro Gomez");

        when(vehiculoService.getVehiculoById(5L)).thenReturn(null);

        mockMvc.perform(put("/vehiculos/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());

        verify(vehiculoService, times(1)).getVehiculoById(5L);
        verify(vehiculoService, never()).save(any(Vehiculo.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testDeleteById_Found() throws Exception {
        when(vehiculoService.getVehiculoById(1L)).thenReturn(vehiculo1);
        doNothing().when(vehiculoService).deleteById(1L);

        mockMvc.perform(delete("/vehiculos/1"))
                .andExpect(status().isOk());

        verify(vehiculoService, times(1)).getVehiculoById(1L);
        verify(vehiculoService, times(1)).deleteById(1L);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testDeleteById_NotFound() throws Exception {
        when(vehiculoService.getVehiculoById(3L)).thenReturn(null);

        mockMvc.perform(delete("/vehiculos/3"))
                .andExpect(status().isNotFound());

        verify(vehiculoService, times(1)).getVehiculoById(3L);
        verify(vehiculoService, never()).deleteById(anyLong());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testSearch() throws Exception {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
        Page<Vehiculo> page = new PageImpl<>(List.of(vehiculo1));

        when(vehiculoService.findByPatenteContainingIgnoreCaseOrNombrePropietarioContainingIgnoreCase("Juan", pageable))
                .thenReturn(page);

        mockMvc.perform(get("/vehiculos/search")
                        .param("keyword", "Juan")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nombrePropietario").value("Juan Perez"));

        verify(vehiculoService, times(1))
                .findByPatenteContainingIgnoreCaseOrNombrePropietarioContainingIgnoreCase("Juan", pageable);
    }

}
