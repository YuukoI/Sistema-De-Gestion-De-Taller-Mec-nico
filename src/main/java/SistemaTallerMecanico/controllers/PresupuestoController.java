package SistemaTallerMecanico.controllers;

import SistemaTallerMecanico.dtos.PresupuestoDTO;
import SistemaTallerMecanico.entities.Presupuesto;
import SistemaTallerMecanico.entities.Vehiculo;
import SistemaTallerMecanico.services.PresupuestoService;
import SistemaTallerMecanico.services.VehiculoService;
import SistemaTallerMecanico.services.pdfService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/presupuestos")
public class PresupuestoController {

    private final PresupuestoService presupuestoService;

    private final VehiculoService vehiculoService;

    private final pdfService pdfService;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Page<Presupuesto>> findAllPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy )
    {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return ResponseEntity.ok(presupuestoService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Presupuesto> findById(@PathVariable Long id){
        Presupuesto presupuesto = presupuestoService.findById(id);
        if(presupuesto == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(presupuesto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Presupuesto> deleteById(@PathVariable Long id){
        Presupuesto presupuesto = presupuestoService.findById(id);
        if(presupuesto == null){
            return ResponseEntity.notFound().build();
        }
        presupuestoService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> save(@Valid @RequestBody PresupuestoDTO presupuestoDTO, BindingResult resultado){
        if (resultado.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            resultado.getFieldErrors().forEach(error ->
                    errores.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errores);
        }

        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setPatente(presupuestoDTO.getPatente());
        presupuesto.setNombrePropietario(presupuestoDTO.getNombrePropietario());
        presupuesto.setManoDeObra(presupuestoDTO.getManoDeObra());
        presupuesto.setTotal(presupuestoDTO.getTotal());
        presupuesto.setDescripcion(presupuestoDTO.getDescripcion());
        presupuesto.setFecha(LocalDate.now());
        presupuesto.setMarca(presupuestoDTO.getMarca());
        presupuesto.setModelo(presupuestoDTO.getModelo());

        Vehiculo vehiculo = vehiculoService.findByPatente(presupuestoDTO.getPatente());

        if(vehiculo == null){
            vehiculo = new Vehiculo();
            vehiculo.setPatente(presupuestoDTO.getPatente());
            vehiculo.setModelo(presupuestoDTO.getModelo());
            vehiculo.setMarca(presupuestoDTO.getMarca());
            vehiculo.setNombrePropietario(presupuestoDTO.getNombrePropietario());

            vehiculoService.save(vehiculo);
        }

        return ResponseEntity.ok(presupuestoService.save(presupuesto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(@Valid @RequestBody PresupuestoDTO presupuestoDTO, @PathVariable Long id, BindingResult resultado){
        if (resultado.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            resultado.getFieldErrors().forEach(error ->
                    errores.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errores);
        }

        Presupuesto presupuesto = presupuestoService.findById(id);
        if(presupuesto == null){
            return ResponseEntity.notFound().build();
        }

        presupuesto.setPatente(presupuestoDTO.getPatente());
        presupuesto.setNombrePropietario(presupuestoDTO.getNombrePropietario());
        presupuesto.setManoDeObra(presupuestoDTO.getManoDeObra());
        presupuesto.setTotal(presupuestoDTO.getTotal());
        presupuesto.setDescripcion(presupuestoDTO.getDescripcion());
        presupuesto.setMarca(presupuestoDTO.getMarca());
        presupuesto.setModelo(presupuestoDTO.getModelo());

        return ResponseEntity.ok(presupuestoService.update(presupuesto));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Page<Presupuesto>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(presupuestoService.findByPatenteContainingIgnoreCaseOrNombrePropietarioContainingIgnoreCase(keyword, pageable));
    }

    @GetMapping("/{id}/pdf")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<byte[]> generarPdf(@PathVariable Long id) {
        Presupuesto presupuesto = presupuestoService.findById(id);
        if (presupuesto == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] pdfBytes = pdfService.generarReciboPdf(presupuesto);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=presupuesto_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
