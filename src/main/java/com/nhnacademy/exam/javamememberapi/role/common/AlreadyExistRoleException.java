package com.nhnacademy.exam.javamememberapi.role.common;

public class AlreadyExistRoleException extends RuntimeException {
    public AlreadyExistRoleException(String message) {
        super(message);
    }
}
