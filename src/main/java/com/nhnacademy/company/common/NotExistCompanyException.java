package com.nhnacademy.company.common;

public class NotExistCompanyException extends RuntimeException {

    public NotExistCompanyException(String message) {
        super(message);
    }
}

