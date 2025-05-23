package com.nhnacademy.company.common;

import com.nhnacademy.common.exception.ResourceNotFoundException;

public class NotExistCompanyException extends ResourceNotFoundException {

    public NotExistCompanyException(String message) {
        super("회사를 찾을 수 없습니다: 도메인 " + message);
    }
}

