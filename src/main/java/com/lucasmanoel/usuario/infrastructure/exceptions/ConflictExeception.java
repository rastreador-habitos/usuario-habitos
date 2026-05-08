package com.lucasmanoel.usuario.infrastructure.exceptions;

public class ConflictExeception extends RuntimeException {
    public ConflictExeception(String message) {
        super(message);
    }
}
