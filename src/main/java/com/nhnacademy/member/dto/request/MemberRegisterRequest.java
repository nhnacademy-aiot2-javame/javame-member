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

    private String memberEmail;

    private String memberPassword;

    private String memberName;

    private String companyDomain;

}
