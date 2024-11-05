package com.example.funko.category.exceptions;

import jakarta.servlet.annotation.HttpConstraint;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CategoryDoesNotExistException extends CategoryException {
    public CategoryDoesNotExistException(String message) {
        super(message);
    }
}
