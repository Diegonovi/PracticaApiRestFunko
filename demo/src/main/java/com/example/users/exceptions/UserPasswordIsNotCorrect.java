package com.example.users.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserPasswordIsNotCorrect extends UserException{
    public UserPasswordIsNotCorrect(String message) {
        super(message);
    }
}
