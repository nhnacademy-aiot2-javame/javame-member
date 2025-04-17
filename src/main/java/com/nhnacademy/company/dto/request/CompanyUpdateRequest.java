package com.nhnacademy.company.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 기존 회사 정보를 수정할 때 필요한 데이터를 담는 DTO 클래스입니다.
 */
@Getter
@NoArgsConstructor // 기본 생성자
@AllArgsConstructor // 모든 필드 포함 생성자
public class CompanyUpdateRequest {

    /**
     * 변경할 회사의 새 이름.
     */
    private String companyName;

    /**
     * 변경할 회사의 새 이메일 (선택 사항).
     */
    private String companyEmail;

    /**
     * 변경할 회사의 새 연락처.
     */
    private String companyMobile;

    /**
     * 변경할 회사의 새 주소.
     */
    private String companyAddress;
}
