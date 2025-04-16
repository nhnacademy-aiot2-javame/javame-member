package com.nhnacademy.member.common;

import com.nhnacademy.common.exception.ResourceAlreadyExistsException;

public class AlreadyExistMemberException extends ResourceAlreadyExistsException {
    public AlreadyExistMemberException(String message) {
        super(message);
    }
}
