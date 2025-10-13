package SistemaTallerMecanico.controllers;

import SistemaTallerMecanico.dtos.UserDTO;
import SistemaTallerMecanico.entities.User;
import SistemaTallerMecanico.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<User>> findAllPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<User> users = userService.findAllPaged(pageable);
        users.forEach(u -> u.setPassword(null));
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Page<User>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<User> resultado = userService.searchByUsernameOrRole(keyword, pageable);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(u -> {
                    u.setPassword(null);
                    return ResponseEntity.ok(u);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO updatedUser) {
        User user = userService.findById(id).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();

        if (updatedUser.getUsername() != null && !updatedUser.getUsername().isEmpty()) {
            Optional<User> existingUser = userService.findByUsername(updatedUser.getUsername());
            if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
        }

        user.setUsername(updatedUser.getUsername());
        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setCountry(updatedUser.getCountry());
        user.setRole(updatedUser.getRole());

        User savedUser = userService.save(user);

        UserDTO response = new UserDTO(
                savedUser.getUsername(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getCountry(),
                savedUser.getRole()
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        User user = userService.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}

