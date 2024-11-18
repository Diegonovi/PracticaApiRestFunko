package com.example.security.userauthentication;

import com.example.security.model.JwtAuthResponse;
import com.example.users.dto.SignInUser;
import com.example.users.dto.SignUpUser;

public interface AuthenticationService {
    JwtAuthResponse signUp(SignUpUser request);

    JwtAuthResponse signIn(SignInUser request);
}