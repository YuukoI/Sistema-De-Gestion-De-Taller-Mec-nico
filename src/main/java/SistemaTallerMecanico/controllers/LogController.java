package SistemaTallerMecanico.controllers;

import SistemaTallerMecanico.entities.Log;
import SistemaTallerMecanico.services.LogService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/logs")
@AllArgsConstructor
public class LogController {

    private final LogService logService;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Page<Log> getLogs(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "15") int size,
                             @RequestParam(defaultValue = "id") String sortBy )
    {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return logService.findAll(pageable);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Page<Log> searchLogs(@RequestParam String query,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "15") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return logService.findByUsernameContainingIgnoreCaseOrActionContainingIgnoreCase(query, query, pageable);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAllLogs() {
        logService.deleteAll();
        return ResponseEntity.noContent().build();
    }
}
