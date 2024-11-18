package com.example.users.services;

import com.example.users.models.User;

public interface UserService {
    User registerUser(User user);
    User getUserById(Long id);
    User updateUser(Long id, User updatedUser);
    User deleteUser(Long id);
    User loadUserByUsername(String username);
}
