package io.bluepipe.client.core;

public class TransportException extends RuntimeException {

    public TransportException(String message) {
        super(message);
    }

    public TransportException(int code, String message) {
        super(message);
    }
}
