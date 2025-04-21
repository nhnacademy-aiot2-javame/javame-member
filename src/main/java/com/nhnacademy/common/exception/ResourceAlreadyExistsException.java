package com.nhnacademy.common.exception;


/**
 * 리소스가 이미 존재할 때 사용되는 예외 클래스입니다.
 * 예: 존재하지 않는 회원, 회사, 역할 등
 */
public class ResourceAlreadyExistsException extends RuntimeException {
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
}