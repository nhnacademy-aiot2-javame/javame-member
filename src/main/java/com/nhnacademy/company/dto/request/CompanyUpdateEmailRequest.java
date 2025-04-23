package com.nhnacademy.company.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 오너의 아이디이면서 회사 대표 이메일인 정보의 업데이트와 관련된 DTO 입니다.
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyUpdateEmailRequest {
    /**
     * 변경하기 전 현재 이메일.
     */
    @JsonProperty
    private String currentEmail;

    /**
     * 변경할 회사의 새 이메일.
     */
    @JsonProperty
    private String newEmail;
}
