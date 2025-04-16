package com.nhnacademy.role.common;

public class NotExistRoleException extends RuntimeException{
    public NotExistRoleException(String message) {
        super(message);
    }
}
