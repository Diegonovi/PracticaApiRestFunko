package com.example.users.services;

import com.example.users.exceptions.UserDoesntExistException;
import com.example.users.models.User;
import com.example.users.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User loadUserByUsername(String username){
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserDoesntExistException("Usuario con username " + username + " no encontrado"));
    }

    @Override
    public User getUserById(Long id) {
        log.info("Buscando el usuario con id: {}", id);
        return null;
    }

    @Override
    public User registerUser(User user) {
        return null;
    }

    @Override
    public User updateUser(Long id, User updatedUser) {
        return null;
    }

    @Override
    public User deleteUser(Long id) {
        return null;
    }
}
