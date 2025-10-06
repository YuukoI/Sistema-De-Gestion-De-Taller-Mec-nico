package SistemaTallerMecanico.controllers;

import SistemaTallerMecanico.dtos.VehiculoDTO;
import SistemaTallerMecanico.entities.Vehiculo;
import SistemaTallerMecanico.services.VehiculoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/vehiculos")
public class VehiculoController {

    private final VehiculoService vehiculoService;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Page<Vehiculo>> findAllPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy)
    {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(vehiculoService.getAllVehiculos(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Vehiculo> findById(@PathVariable Long id) {
        Vehiculo vehiculo = vehiculoService.getVehiculoById(id);
        if(vehiculo == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(vehiculo);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        Vehiculo vehiculo = vehiculoService.getVehiculoById(id);
        if(vehiculo == null){
            return ResponseEntity.notFound().build();
        }
        vehiculoService.deleteById(id);

        return  ResponseEntity.ok().build();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> save(@Valid @RequestBody VehiculoDTO vehiculoDTO, BindingResult resultado){
        if (resultado.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            resultado.getFieldErrors().forEach(error ->
                    errores.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errores);
        }
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setPatente(vehiculoDTO.getPatente());
        vehiculo.setModelo(vehiculoDTO.getModelo());
        vehiculo.setMarca(vehiculoDTO.getMarca());
        vehiculo.setNombrePropietario(vehiculoDTO.getNombrePropietario());

        return ResponseEntity.ok().body(vehiculoService.save(vehiculo));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(@Valid @RequestBody VehiculoDTO vehiculoDTO, BindingResult resultado, @PathVariable Long id) {
        if (resultado.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            resultado.getFieldErrors().forEach(error ->
                    errores.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errores);
        }

        Vehiculo vehiculo = vehiculoService.getVehiculoById(id);
        if(vehiculo == null){
            return ResponseEntity.notFound().build();
        }
        vehiculo.setId(id);
        vehiculo.setPatente(vehiculoDTO.getPatente());
        vehiculo.setModelo(vehiculoDTO.getModelo());
        vehiculo.setMarca(vehiculoDTO.getMarca());
        vehiculo.setNombrePropietario(vehiculoDTO.getNombrePropietario());

        return  ResponseEntity.ok().body(vehiculoService.save(vehiculo));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Page<Vehiculo>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(vehiculoService.findByPatenteContainingIgnoreCaseOrNombrePropietarioContainingIgnoreCase(keyword, pageable));
    }
}
