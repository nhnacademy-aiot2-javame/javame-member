package com.nhnacademy.role.common;

import com.nhnacademy.common.exception.ResourceAlreadyExistsException;

public class AlreadyExistRoleException extends ResourceAlreadyExistsException {
    public AlreadyExistRoleException(String message) {
        super(message);
    }
}
