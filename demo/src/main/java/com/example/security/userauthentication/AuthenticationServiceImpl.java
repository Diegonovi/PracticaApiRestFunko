package com.example.security.userauthentication;

import com.example.security.jwt.JwtService;
import com.example.security.model.JwtAuthResponse;
import com.example.users.dto.SignInUser;
import com.example.users.dto.SignUpUser;
import com.example.users.exceptions.UserPasswordIsNotCorrect;
import com.example.users.exceptions.UserPasswordsDontMatchException;
import com.example.users.models.Role;
import com.example.users.models.User;
import com.example.users.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

/**
 * Implementación de nuestro servicio de autenticación
 */
@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthenticationServiceImpl(UserService userService, PasswordEncoder passwordEncoder,
                                     JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Registra un usuario
     *
     * @param request datos del usuario
     * @return Token de autenticación
     */
    @Override
    public JwtAuthResponse signUp(SignUpUser request) {
        log.info("Creando usuario: {}", request);
        if (request.getPassword().contentEquals(request.getRepeatedPassword())) {
            User user = User.builder()
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .name(request.getName())
                    .lastName(request.getLastName())
                    .roles(Stream.of(Role.USER).toList())
                    .build();
            var userStored = userService.registerUser(user);
            return JwtAuthResponse.builder().token(jwtService.generateToken(userStored)).build();
        } else {
            throw new UserPasswordsDontMatchException("Las contraseñas no coinciden");

        }
    }

    /**
     * Autentica un usuario
     *
     * @param request datos del usuario
     * @return Token de autenticación
     */
    @Override
    public JwtAuthResponse signIn(SignInUser request) {
        log.info("Autenticando usuario: {}", request);
        // Autenticamos y devolvemos el token
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        var user = userService.findUserByUsername(request.getUsername());
        if (!request.getPassword().contentEquals(user.getPassword())) throw new UserPasswordIsNotCorrect("La contraseña no es correcta");
        var jwt = jwtService.generateToken(user);
        return JwtAuthResponse.builder().token(jwt).build();
    }
}