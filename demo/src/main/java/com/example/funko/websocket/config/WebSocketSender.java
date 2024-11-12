package com.example.funko.websocket.config;

import java.io.IOException;

/**
 * Interfaz para enviar mensajes por WebSockets
 */
public interface WebSocketSender {

    void sendMessage(String message) throws IOException;

    void sendPeriodicMessages() throws IOException;
}