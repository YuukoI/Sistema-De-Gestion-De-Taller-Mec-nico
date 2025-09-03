package SistemaTallerMecanico.services;

import SistemaTallerMecanico.auth.AuthResponse;
import SistemaTallerMecanico.auth.LoginRequest;
import SistemaTallerMecanico.auth.RegisterRequest;

public interface AuthService {

    public AuthResponse login(LoginRequest loginRequest);

    public AuthResponse register(RegisterRequest registerRequest);
}
