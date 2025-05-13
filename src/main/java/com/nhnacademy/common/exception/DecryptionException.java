package com.nhnacademy.common.exception;

public class DecryptionException extends RuntimeException {
    public DecryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
