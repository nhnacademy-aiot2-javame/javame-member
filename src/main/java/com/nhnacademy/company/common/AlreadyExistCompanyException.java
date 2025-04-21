package com.nhnacademy.company.common;

import com.nhnacademy.common.exception.ResourceAlreadyExistsException;

public class AlreadyExistCompanyException extends ResourceAlreadyExistsException {
    public AlreadyExistCompanyException(String message) {
        super(message);
    }
}
