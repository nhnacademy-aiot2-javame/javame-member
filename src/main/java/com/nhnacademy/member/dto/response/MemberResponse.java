package com.nhnacademy.member.dto.response;

import com.fasterxml.jackson.core.JsonToken;
import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 회원 정보 조회 응답 시 반환될 데이터를 담는 DTO 클래스입니다.
 * 비밀번호 등 민감 정보는 포함하지 않습니다.
 */
@Getter
@NoArgsConstructor
@Builder
public class MemberResponse {

    /**
     * 회원 식별 번호.
     */
    private Long memberNo;

    /**
     * 회원 이메일.
     */
    private String memberEmail;

    /**
     * 소속 회사 도메인.
     */
    private String companyDomain;

    /**
     * 역할 ID (예: "ROLE_USER").
     */
    private String roleId;

    private LocalDateTime registerAt;

    private LocalDateTime lastLoginAt;

    @QueryProjection
    public MemberResponse(Long memberNo, String memberEmail, String companyDomain, String roleId, LocalDateTime registerAt, LocalDateTime lastLoginAt) {
        this.memberNo = memberNo;
        this.memberEmail = memberEmail;
        this.companyDomain = companyDomain;
        this.roleId = roleId;
        this.registerAt = registerAt;
        this.lastLoginAt = lastLoginAt;
    }
}
