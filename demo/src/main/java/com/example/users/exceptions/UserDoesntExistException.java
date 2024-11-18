package com.example.users.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserDoesntExistException extends UserException {
    public UserDoesntExistException(String message) {
        super(message);
    }
}
