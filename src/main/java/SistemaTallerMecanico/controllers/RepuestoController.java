package SistemaTallerMecanico.controllers;
import SistemaTallerMecanico.dtos.RepuestoDTO;
import SistemaTallerMecanico.entities.Repuesto;
import SistemaTallerMecanico.services.RepuestoService;
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
import java.util.HashMap;
import java.util.Map;
@RestController
@AllArgsConstructor
@RequestMapping("/repuestos")
public class RepuestoController {

    private final RepuestoService repuestoService;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Page<Repuesto>> findAllPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy )
    {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return ResponseEntity.ok(repuestoService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Repuesto> findById(@PathVariable Long id) {
        Repuesto repuesto = repuestoService.findById(id);
        if(repuesto == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(repuesto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Repuesto> deleteById(@PathVariable Long id) {
        Repuesto repuesto = repuestoService.findById(id);
        if(repuesto == null){
            return ResponseEntity.notFound().build();
        }
        repuestoService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> save(@Valid @RequestBody RepuestoDTO repuestoDTO, BindingResult resultado) {
        if (resultado.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            resultado.getFieldErrors()
                    .forEach(error -> errores.put(error.getField(), error.getDefaultMessage()) );
            return ResponseEntity.badRequest().body(errores);
        }

        Repuesto repuesto = new Repuesto();
        repuesto.setNombre(repuestoDTO.getNombre());
        repuesto.setDescripcion(repuestoDTO.getDescripcion());
        repuesto.setPrecio(repuestoDTO.getPrecio());
        repuesto.setStock(repuestoDTO.getStock());
        return ResponseEntity.ok().body(repuestoService.save(repuesto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody RepuestoDTO repuestoDTO, BindingResult resultado)
    { if (resultado.hasErrors()) {
        Map<String, String> errores = new HashMap<>();
        resultado.getFieldErrors()
                .forEach(error -> errores.put(error.getField(), error.getDefaultMessage()) );
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
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Page<Repuesto>> search(
            @RequestParam(required = false) String nombre,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size )
    {
        Pageable pageable = PageRequest.of(page, size);
        if (nombre != null && !nombre.isEmpty()) {
            return ResponseEntity.ok(repuestoService.findByNombre(nombre, pageable));
        }
        return ResponseEntity.ok(repuestoService.findAll(pageable));
    }

    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Repuesto> actualizarStock(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        int nuevoStock = body.get("stock");
        Repuesto repuesto = repuestoService.actualizarStock(id, nuevoStock);
        return ResponseEntity.ok(repuesto);
    }
}