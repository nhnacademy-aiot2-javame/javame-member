package com.nhnacademy.exam.javamememberapi.member.common;

public class NotExistMemberException extends RuntimeException {

    public NotExistMemberException(String message) {
        super(message);
    }

    }

