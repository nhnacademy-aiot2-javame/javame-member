package com.nhnacademy.member.dto;

import lombok.ToString;

import java.time.LocalDate;

@ToString
public class MemberResponse {

    private final Long memberNo;

    private final String memberId;

    private final String memberName;

    private final LocalDate memberBirth;

    private final String memberEmail;

    private final String memberMobile;

    private final String memberSex;

    private final String roleId;

    public MemberResponse(Long memberNo, String memberId, String memberName, LocalDate memberBirth, String memberEmail, String memberMobile, String memberSex, String roleId) {
        this.memberNo = memberNo;
        this.memberId = memberId;
        this.memberName = memberName;
        this.memberBirth = memberBirth;
        this.memberEmail = memberEmail;
        this.memberMobile = memberMobile;
        this.memberSex = memberSex;
        this.roleId = roleId;
    }

    public Long getMemberNo() {
        return memberNo;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public LocalDate getMemberBirth() {
        return memberBirth;
    }

    public String getMemberEmail() {
        return memberEmail;
    }

    public String getMemberMobile() {
        return memberMobile;
    }

    public String getMemberSex() {
        return memberSex;
    }

    public String getRoleId() {
        return roleId;
    }
}
