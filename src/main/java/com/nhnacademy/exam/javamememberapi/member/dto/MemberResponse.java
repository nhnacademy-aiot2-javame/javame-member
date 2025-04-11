package com.nhnacademy.exam.javamememberapi.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MemberResponse {

    private final Long memberNo;

    private final String memberId;

    private final String memberName;

    private final String memberEmail;

    private final String memberMobile;

    private final String memberSex;

    private final String roleId;

    public MemberResponse(Long memberNo, String memberId, String memberName, String memberEmail, String memberMobile, String memberSex, String roleId) {
        this.memberNo = memberNo;
        this.memberId = memberId;
        this.memberName = memberName;
        this.memberEmail = memberEmail;
        this.memberMobile = memberMobile;
        this.memberSex = memberSex;
        this.roleId = roleId;
    }
}
