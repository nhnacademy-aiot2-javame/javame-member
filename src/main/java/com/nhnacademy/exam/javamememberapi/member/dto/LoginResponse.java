package com.nhnacademy.exam.javamememberapi.member.dto;

public class LoginResponse {
    private final String memberId;

    private final String memberPassword;


    public LoginResponse(String memberId, String memberPassword) {
        this.memberId = memberId;
        this.memberPassword = memberPassword;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getMemberPassword() {
        return memberPassword;
    }
}
