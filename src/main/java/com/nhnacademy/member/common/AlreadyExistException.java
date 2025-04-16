package com.nhnacademy.member.common;

public class AlreadyExistException extends RuntimeException{
    public AlreadyExistException(String message){
        super(message);
    }

}
