package com.example.funko.category.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CategoryAlreadyExistsException extends CategoryException {
    public CategoryAlreadyExistsException(String message) {
        super(message);
    }
}
