package com.nhnacademy.common.exception;

/**
 * 요청한 리소스를 찾을 수 없을 때 사용되는 예외 클래스입니다.
 * 예: 존재하지 않는 회원, 회사, 역할 등
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}