package com.nhnacademy.member.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원 가입 요청 시 필요한 데이터를 담는 DTO 클래스입니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberRegisterRequest {

    /**
     * 회원 이메일(로그인시 id로 사용됩니다.)
     */
    private String memberEmail;

    /**
     * 회원 비밀번호.
     */
    private String memberPassword;

    /**
     * 회사 도메인
     */
    private String companyDomain;

}
