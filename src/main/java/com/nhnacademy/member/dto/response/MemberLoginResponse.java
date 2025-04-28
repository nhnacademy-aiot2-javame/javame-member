package com.nhnacademy.member.dto.response; // 응답 DTO 패키지

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 다른 서비스(예: Auth API)에 로그인 관련 정보를 제공하기 위한 DTO입니다.
 * 해싱된 비밀번호를 포함합니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberLoginResponse {

    /**
     * 회원 식별 아이디.
     */
    private Long memberNo;

    /**
     * 회원 이메일.
     */
    private String memberEmail;

    /**
     * 회원 비밀번호.
     */
    private String memberPassword;

    /**
     * 권한 아이디.
     */
    private String roleId;
}
