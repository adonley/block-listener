package io.block16.ethlistener.exceptions;

public class UpstreamException extends RuntimeException {
    public UpstreamException() { }
    public UpstreamException(String message) {
        super(message);
    }
}
