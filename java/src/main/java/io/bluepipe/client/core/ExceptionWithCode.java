package io.bluepipe.client.core;

public class ExceptionWithCode extends RuntimeException {

    private final int code;

    public ExceptionWithCode(String message, int code) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
