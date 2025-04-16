package com.nhnacademy.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginResponse {

    @JsonProperty("memberId")
    private final String memberId;

    @JsonProperty("memberPassword")
    private final String memberPassword;

    @JsonProperty("roleId")
    private final String roleId;

    public LoginResponse(String memberId, String memberPassword, String roleId) {
        this.memberId = memberId;
        this.memberPassword = memberPassword;
        this.roleId = roleId;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getMemberPassword() {
        return memberPassword;
    }

    public String getRoleId() {
        return roleId;
    }
}
