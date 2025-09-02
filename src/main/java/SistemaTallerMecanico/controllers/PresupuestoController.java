package SistemaTallerMecanico.controllers;

import SistemaTallerMecanico.dtos.PresupuestoDTO;
import SistemaTallerMecanico.entities.Presupuesto;
import SistemaTallerMecanico.services.PresupuestoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/presupuestos")
public class PresupuestoController {

    private final PresupuestoService presupuestoService;

    @GetMapping
    public ResponseEntity<List<Presupuesto>> findAll(){
        List<Presupuesto> presupuestos = presupuestoService.findAll();
        if(presupuestos.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(presupuestos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Presupuesto> findById(@PathVariable Long id){
        Presupuesto presupuesto = presupuestoService.findById(id);
        if(presupuesto == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(presupuesto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Presupuesto> deleteById(@PathVariable Long id){
        Presupuesto presupuesto = presupuestoService.findById(id);
        if(presupuesto == null){
            return ResponseEntity.notFound().build();
        }
        presupuestoService.deleteById(id);
        return ResponseEntity.ok().body(presupuesto);
    }

    @PostMapping
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
        presupuesto.setRepuestos(presupuestoDTO.getRepuestos());
        presupuesto.setManoDeObra(presupuestoDTO.getManoDeObra());
        presupuesto.setTotal(presupuestoDTO.getTotal());
        presupuesto.setDescripcion(presupuestoDTO.getDescripcion());

        return ResponseEntity.ok(presupuestoService.save(presupuesto));
    }

    @PutMapping("/{id}")
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
        presupuesto.setRepuestos(presupuestoDTO.getRepuestos());
        presupuesto.setManoDeObra(presupuestoDTO.getManoDeObra());
        presupuesto.setTotal(presupuestoDTO.getTotal());
        presupuesto.setDescripcion(presupuestoDTO.getDescripcion());

        return ResponseEntity.ok(presupuestoService.update(presupuesto));
    }
}
