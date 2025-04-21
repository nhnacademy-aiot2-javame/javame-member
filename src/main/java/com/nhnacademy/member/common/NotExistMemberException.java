package com.nhnacademy.member.common;

import com.nhnacademy.common.exception.ResourceNotFoundException;

public class NotExistMemberException extends ResourceNotFoundException {

    public NotExistMemberException(String message) {
        super(message);
    }
}

