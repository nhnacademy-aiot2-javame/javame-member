package com.nhnacademy.member.common;

public class NotMatchesPasswordException extends RuntimeException {
    public NotMatchesPasswordException(Long mbNo) {
        super(String.format("비밀번호 변경 실패: 현재 비밀번호 불일치. ID: %d", mbNo));
    }
}
