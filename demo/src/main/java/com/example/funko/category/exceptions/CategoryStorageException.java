package com.example.funko.category.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CategoryStorageException extends CategoryException {
    public CategoryStorageException(String message) {
        super(message);
    }
}
