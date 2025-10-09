package SistemaTallerMecanico.services;

import SistemaTallerMecanico.auth.AuthResponse;
import SistemaTallerMecanico.auth.LoginRequest;
import SistemaTallerMecanico.auth.RegisterRequest;

public interface AuthService {

    AuthResponse login(LoginRequest loginRequest);

    AuthResponse register(RegisterRequest registerRequest);
}
