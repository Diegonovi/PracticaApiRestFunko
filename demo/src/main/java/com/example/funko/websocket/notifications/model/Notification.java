package com.example.funko.websocket.notifications.model;

public record Notification<T>(
        String entity,
        Tipo type,
        T data,
        String createdAt
) {

    public enum Tipo {CREATE, UPDATE, DELETE}

}