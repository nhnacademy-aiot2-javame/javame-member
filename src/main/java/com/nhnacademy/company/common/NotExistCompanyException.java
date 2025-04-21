package com.nhnacademy.company.common;

import com.nhnacademy.common.exception.ResourceNotFoundException;

public class NotExistCompanyException extends ResourceNotFoundException {

    public NotExistCompanyException(String message) {
        super(message);
    }
}

