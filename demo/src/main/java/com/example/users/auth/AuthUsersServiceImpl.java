package com.example.users.auth;

import com.example.users.exceptions.UserDoesntExistException;
import com.example.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

// Indicamos que es uns ervicio de detalles de usuario
// Es muy importante esta línea para decir que vamos a usar el servicio de usuarios Spring
// Otra forma de hacerlo es

/**
 * public interface AuthUsersService {
 * UserDetailsService userDetailsService();
 * }
 * <p>
 * y lugeo usarlo aqui con implements AuthUsersService
 */
@Service("userDetailsService")
public class AuthUsersServiceImpl implements AuthUsersService {

    private final UserRepository authUsersRepository;

    @Autowired
    public AuthUsersServiceImpl(UserRepository authUsersRepository) {
        this.authUsersRepository = authUsersRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) {
        return authUsersRepository.findByUsername(username)
                .orElseThrow(() -> new UserDoesntExistException("Usuario con username " + username + " no encontrado"));
    }
}