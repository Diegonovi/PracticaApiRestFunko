package com.example.orders.exceptions;

public class InvalidOrderException extends OrderException{
    public InvalidOrderException(String message) {
        super(message);
    }
}
