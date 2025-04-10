package com.nhnacademy.exam.javamememberapi.member.dto;

import lombok.Setter;

@Setter
public class LoginRequest {

    private final String memberId;

    private final String memberPassword;


    public LoginRequest(String memberId, String memberPassword) {
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
