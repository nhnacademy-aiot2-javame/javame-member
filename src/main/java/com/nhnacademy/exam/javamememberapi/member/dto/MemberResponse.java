package com.nhnacademy.exam.javamememberapi.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class MemberResponse {

    private final Long memberNo;

    private final String memberId;

    private final String memberName;

    private final String memberEmail;

    private final String memberSex;


    public MemberResponse(Long memberNo, String memberId, String memberName, String memberEmail, String memberSex) {
        this.memberNo = memberNo;
        this.memberId = memberId;
        this.memberName = memberName;
        this.memberEmail = memberEmail;
        this.memberSex = memberSex;
    }
}
