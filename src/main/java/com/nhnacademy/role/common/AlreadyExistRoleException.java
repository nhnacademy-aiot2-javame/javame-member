package com.nhnacademy.role.common;

public class AlreadyExistRoleException extends RuntimeException {
    public AlreadyExistRoleException(String message) {
        super(message);
    }
}
