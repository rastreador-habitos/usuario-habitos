package com.lucasmanoel.usuario.infrastructure.exceptions;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
    public UnauthorizedException(String message, Throwable throwable){
        super(message, throwable);
    }
}
