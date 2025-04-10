package com.nhnacademy.exam.javamememberapi.role.common;

public class NotExistRoleException extends RuntimeException{
    public NotExistRoleException(String message) {
        super(message);
    }
}
