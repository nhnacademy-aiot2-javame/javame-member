package com.nhnacademy.company.common;

public class AlreadyExistCompanyException extends RuntimeException {
    public AlreadyExistCompanyException(String message) {
        super(message);
    }
}
