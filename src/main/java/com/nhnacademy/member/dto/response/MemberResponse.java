package com.nhnacademy.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원 정보 조회 응답 시 반환될 데이터를 담는 DTO 클래스입니다.
 * 비밀번호 등 민감 정보는 포함하지 않습니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {

    private String memberId;       // 회원 UUID
    private String memberEmail;    // 회원 이메일
    private String memberName;     // 회원 이름
    private String companyDomain;  // 소속 회사 도메인
    private String roleId;         // 역할 ID (예: "ROLE_USER")
}
