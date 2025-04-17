package com.nhnacademy.company.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 신규 회사 등록을 요청할 때 필요한 데이터를 담는 DTO 클래스입니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyRegisterRequest {

    /**
     * 등록할 회사의 고유 도메인 (기본키).
     */
    private String companyDomain;

    /**
     * 등록할 회사의 이름.
     */
    private String companyName;

    /**
     * 등록할 회사의 대표 이메일.
     */
    private String companyEmail;

    /**
     * 등록할 회사의 대표 연락처.
     */
    private String companyMobile;

    /**
     * 등록할 회사의 주소.
     */
    private String companyAddress;
}
