package com.nhnacademy.company.service;


import com.nhnacademy.company.dto.request.CompanyUpdateEmailRequest;
import com.nhnacademy.company.dto.request.CompanyUpdateRequest;
import com.nhnacademy.company.dto.request.CompanyRegisterRequest;
import com.nhnacademy.company.dto.response.CompanyResponse;

import java.util.List;

/**
 * 회사(Company) 관련 비즈니스 로직을 처리하는 서비스 인터페이스입니다.
 * 회사 등록, 조회, 수정 등의 기능을 제공합니다.
 */
public interface CompanyService {

    /**
     * 신규 회사를 등록하고, 동시에 첫 번째 관리자(Owner) 회원을 생성합니다.
     * 이 작업은 단일 트랜잭션으로 처리되어 원자성을 보장합니다.
     * 회사 도메인과 Owner의 이메일은 시스템 내에서 고유해야 합니다.
     *
     * @param request 회사 정보 및 첫 Owner 회원 정보가 담긴 {@link CompanyRegisterRequest} DTO
     * @return 등록 완료된 회사의 정보 ({@link CompanyResponse})
     */
    CompanyResponse registerCompany(CompanyRegisterRequest request);


    /**
     * 회사 도메인(ID)을 사용하여 특정 회사의 상세 정보를 조회합니다.
     *
     * @param companyDomain 조회할 회사의 고유 도메인
     * @return 조회된 회사의 정보 ({@link CompanyResponse})
     */
    CompanyResponse getCompanyByDomain(String companyDomain);

    /**
     * 기존 회사의 정보(이름, 연락처, 주소)를 수정합니다.
     * 회사 도메인(PK)은 변경할 수 없습니다.
     *
     * @param companyDomain 수정할 회사의 고유 도메인
     * @param request       수정할 정보가 담긴 {@link CompanyUpdateRequest} DTO
     * @return 정보가 수정된 후의 회사 정보 ({@link CompanyResponse})
     */
    CompanyResponse updateCompany(String companyDomain, CompanyUpdateRequest request);


    /**
     * 회사 대표 이메일이자 오너의 아이디값인 이메일을 수정합니다.
     * 회사 도메인(PK)은 변경할 수 없습니다.
     *
     * @param companyDomain 수정할 회사의 고유 도메인
     * @param request       수정할 정보가 담긴 {@link CompanyUpdateEmailRequest} DTO
     * @return 정보가 수정된 후의 회사 정보 ({@link CompanyResponse})
     */
    CompanyResponse updateCompanyEmail(String companyDomain, CompanyUpdateEmailRequest request);

    /**
     * 회사를 비활성 상태로 변경합니다. (논리적 삭제/Soft Delete 개념)
     * 비활성화된 회사와 관련된 정책(예: 소속 멤버 로그인 제한)은 추가 구현이 필요할 수 있습니다.
     *
     * @param companyDomain 비활성화할 회사의 고유 도메인
     */
    void deactivateCompany(String companyDomain);

    /**
     * 비활성 상태인 회사를 다시 활성 상태로 변경합니다.
     *
     * @param companyDomain 활성화할 회사의 고유 도메인
     */
    void activateCompany(String companyDomain);

    /**
     * 시스템에 등록된 모든 회사의 목록을 조회합니다.
     * (추후: 회사 수가 매우 많을 경우 성능 저하 및 메모리 문제가 발생할 수 있으므로,
     * 페이징 처리 또는 특정 조건 필터링 기능 추가를 고려해야 합니다.)
     *
     * @return 모든 회사의 정보 목록 ({@link CompanyResponse})
     */
    List<CompanyResponse> getAllCompanies();

}
