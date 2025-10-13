package SistemaTallerMecanico.services;

import SistemaTallerMecanico.auth.AuthResponse;
import SistemaTallerMecanico.auth.LoginRequest;
import SistemaTallerMecanico.auth.RegisterRequest;
import SistemaTallerMecanico.entities.Role;
import SistemaTallerMecanico.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImp implements AuthService {

    private final UserService userService;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        UserDetails userDetails = userService.findByUsername(loginRequest.getUsername()).orElseThrow();

        String token = jwtService.getToken(userDetails);

        return AuthResponse.builder().token(token).build();

    }

    @Override
    public AuthResponse register(RegisterRequest registerRequest) {

        if (userService.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new RuntimeException("El username ya está en uso");
        }

        User user = User.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .firstName(registerRequest.getFirstname())
                .lastName(registerRequest.getLastname())
                .country(registerRequest.getCountry())
                .role(Role.USER)
                .build();

        userService.save(user);

        return AuthResponse.builder()
                .token(jwtService.getToken(user))
                .build();
    }

}