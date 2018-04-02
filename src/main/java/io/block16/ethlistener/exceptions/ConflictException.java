package io.block16.ethlistener.exceptions;

public class ConflictException extends RuntimeException {
    public ConflictException() {
    }

    public ConflictException(String message) {
        super(message);
    }
}
