package io.bluepipe.client.core;

public class ServiceException extends ExceptionWithCode {

    public ServiceException(String message, int code) {
        super(message, code);
    }
}
