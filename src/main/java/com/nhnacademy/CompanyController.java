package com.nhnacademy; // 패키지 경로 확인

import com.nhnacademy.company.dto.request.CompanyUpdateEmailRequest;
import com.nhnacademy.company.dto.request.CompanyWithOwnerRegisterRequest;
import com.nhnacademy.company.dto.request.CompanyUpdateRequest;
import com.nhnacademy.company.dto.response.CompanyResponse;
import com.nhnacademy.company.service.CompanyService;

import com.nhnacademy.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 회사(Company) 관련 HTTP 요청을 처리하는 REST 컨트롤러입니다.
 * 회사 등록(+Owner 생성), 조회, 수정, (비)활성화 등의 API 엔드포인트를 제공합니다.
 * 모든 경로는 "/companies"를 기본으로 합니다.
 */
@RestController
@RequestMapping(value = "/companies", produces = MediaType.APPLICATION_JSON_VALUE) // 기본 경로 및 JSON 형태 응답 타입
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;
    private final MemberService memberService;

    /**
     * 신규 회사를 등록하고 동시에 첫 번째 관리자(Owner) 회원을 생성합니다.
     * 성공 시 HTTP 상태 코드 201 (Created)과 생성된 회사 정보를 반환합니다.
     *
     * @param request 회사 정보 및 Owner 회원 정보 DTO ({@link CompanyWithOwnerRegisterRequest})
     * @return 생성된 회사 정보 ({@link CompanyResponse})와 상태 코드 201
     */
    @PostMapping
    public ResponseEntity<CompanyResponse> registerCompanyWithOwner(
            @Validated @RequestBody CompanyWithOwnerRegisterRequest request) {
        CompanyResponse response = companyService.registerCompanyWithOwner(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 주어진 회사 도메인에 해당하는 회사 정보를 조회합니다.
     * 성공 시 HTTP 상태 코드 200 (OK)과 회사 정보를 반환합니다.
     *
     * @param companyDomain 조회할 회사의 도메인 (경로 변수)
     * @return 조회된 회사 정보 ({@link CompanyResponse})와 상태 코드 200
     */
    @GetMapping("/{companyDomain}")
    public ResponseEntity<CompanyResponse> getCompanyByDomain(
            @PathVariable String companyDomain) {
        CompanyResponse response = companyService.getCompanyByDomain(companyDomain);
        return ResponseEntity.ok(response);
    }

    /**
     * 시스템에 등록된 모든 회사 목록을 조회합니다.
     * 성공 시 HTTP 상태 코드 200 (OK)과 회사 정보 리스트를 반환합니다.
     *
     * @return 모든 회사 정보 리스트 ({@link CompanyResponse})와 상태 코드 200
     */
    @GetMapping
    public ResponseEntity<List<CompanyResponse>> getAllCompanies() {
        List<CompanyResponse> response = companyService.getAllCompanies();
        return ResponseEntity.ok(response);
    }

    /**
     * 주어진 회사 도메인에 해당하는 회사의 정보(이름, 연락처, 주소)를 수정합니다.
     * 성공 시 HTTP 상태 코드 200 (OK)과 수정된 회사 정보를 반환합니다.
     *
     * @param companyDomain 수정할 회사의 도메인 (경로 변수)
     * @param request       수정할 정보 DTO ({@link CompanyUpdateRequest})
     * @return 수정된 회사 정보 ({@link CompanyResponse})와 상태 코드 200
     */
    @PutMapping("/{companyDomain}")
    public ResponseEntity<CompanyResponse> updateCompany(
            @PathVariable String companyDomain,
            @Validated @RequestBody CompanyUpdateRequest request) {
        CompanyResponse response = companyService.updateCompany(companyDomain, request);
        return ResponseEntity.ok(response);
    }


    /**
     * 주어진 회사 도메인에 해당하는 회사의 정보(이름, 이메일, 연락처, 주소)를 수정합니다.
     * 성공 시 HTTP 상태 코드 200 (OK)과 수정된 회사 정보를 반환합니다.
     *
     * @param companyDomain 수정할 회사의 도메인 (경로 변수)
     * @param request       수정할 정보 DTO ({@link CompanyUpdateEmailRequest})
     * @return 수정된 회사 정보 ({@link CompanyResponse})와 상태 코드 200
     */
    @PutMapping("/{companyDomain}/email")
    public ResponseEntity<CompanyResponse> updateCompanyEmail(
            @PathVariable String companyDomain,
            @Validated @RequestBody CompanyUpdateEmailRequest request) {
        CompanyResponse response = companyService.updateCompanyEmail(companyDomain, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 주어진 회사 도메인에 해당하는 회사를 비활성 상태로 변경합니다.
     * 성공 시 HTTP 상태 코드 204 (No Content)를 반환합니다.
     *
     * @param companyDomain 비활성화할 회사의 도메인 (경로 변수)
     * @return 상태 코드 204 (No Content)
     */
    @PatchMapping("/{companyDomain}/deactivate") // PATCH 사용
    public ResponseEntity<Void> deactivateCompany(
            @PathVariable String companyDomain) {
        companyService.deactivateCompany(companyDomain);
        return ResponseEntity.noContent().build();
    }

    /**
     * 주어진 회사 도메인에 해당하는 회사를 활성 상태로 변경합니다.
     * 성공 시 HTTP 상태 코드 204 (No Content)를 반환합니다.
     *
     * @param companyDomain 활성화할 회사의 도메인 (경로 변수)
     * @return 상태 코드 204 (No Content)
     */
    @PatchMapping("/{companyDomain}/activate") //  PATCH 사용
    public ResponseEntity<Void> activateCompany(
            @PathVariable String companyDomain) {
        companyService.activateCompany(companyDomain);
        return ResponseEntity.noContent().build();
    }

}
