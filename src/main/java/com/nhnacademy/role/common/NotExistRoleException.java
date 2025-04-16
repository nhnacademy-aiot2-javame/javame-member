package com.nhnacademy.role.common;

import com.nhnacademy.common.exception.ResourceNotFoundException;

public class NotExistRoleException extends ResourceNotFoundException {
    public NotExistRoleException(String message) {
        super(message);
    }
}
