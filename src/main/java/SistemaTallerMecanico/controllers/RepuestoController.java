package SistemaTallerMecanico.controllers;

import SistemaTallerMecanico.dtos.RepuestoDTO;
import SistemaTallerMecanico.entities.Repuesto;
import SistemaTallerMecanico.services.RepuestoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/repuestos")
public class RepuestoController {

    private final RepuestoService repuestoService;

    @GetMapping
    public ResponseEntity<List<Repuesto>> findAll() {
        List<Repuesto> repuestos = repuestoService.findAll();

        if(repuestos.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(repuestos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Repuesto> findById(@PathVariable Long id) {
        Repuesto repuesto = repuestoService.findById(id);
        if(repuesto == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(repuesto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Repuesto> deleteById(@PathVariable Long id) {
        Repuesto repuesto = repuestoService.findById(id);
        if(repuesto == null){
            return ResponseEntity.notFound().build();
        }
        repuestoService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody RepuestoDTO repuestoDTO, BindingResult resultado) {
        if (resultado.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            resultado.getFieldErrors().forEach(error ->
                    errores.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errores);
        }

        Repuesto repuesto = new Repuesto();
        repuesto.setNombre(repuestoDTO.getNombre());
        repuesto.setDescripcion(repuestoDTO.getDescripcion());
        repuesto.setPrecio(repuestoDTO.getPrecio());
        repuesto.setStock(repuestoDTO.getStock());

        return  ResponseEntity.ok().body(repuestoService.save(repuesto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody RepuestoDTO repuestoDTO, BindingResult resultado) {
        if (resultado.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            resultado.getFieldErrors().forEach(error ->
                    errores.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errores);
        }

        Repuesto repuestoGuardado = repuestoService.findById(id);
        if(repuestoGuardado == null){
            return ResponseEntity.notFound().build();
        }

        Repuesto repuesto = new Repuesto();
        repuesto.setNombre(repuestoDTO.getNombre());
        repuesto.setDescripcion(repuestoDTO.getDescripcion());
        repuesto.setPrecio(repuestoDTO.getPrecio());
        repuesto.setStock(repuestoDTO.getStock());
        repuesto.setId(repuestoGuardado.getId());

        repuestoService.save(repuesto);

        return ResponseEntity.ok().body(repuesto);
    }
}
