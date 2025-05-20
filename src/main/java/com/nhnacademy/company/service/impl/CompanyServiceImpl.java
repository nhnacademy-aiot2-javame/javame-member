package com.nhnacademy.company.service.impl;

import com.nhnacademy.common.util.AESUtil;
import com.nhnacademy.common.util.HashUtil;
import com.nhnacademy.company.common.AlreadyExistCompanyException;
import com.nhnacademy.company.common.NotExistCompanyException;
import com.nhnacademy.company.common.NotFoundCompanyByEmailException;
import com.nhnacademy.company.domain.Company;
import com.nhnacademy.company.domain.CompanyIndex;
import com.nhnacademy.company.dto.request.CompanyUpdateEmailRequest;
import com.nhnacademy.company.dto.request.CompanyUpdateRequest;
import com.nhnacademy.company.dto.request.CompanyRegisterRequest;
import com.nhnacademy.company.dto.response.CompanyResponse;
import com.nhnacademy.company.repository.CompanyIndexRepository;
import com.nhnacademy.company.repository.CompanyRepository;
import com.nhnacademy.company.service.CompanyService;
import com.nhnacademy.member.domain.Member;
import com.nhnacademy.member.repository.MemberRepository;
import com.nhnacademy.role.domain.Role;
import com.nhnacademy.role.repository.RoleRepository;
import jakarta.ws.rs.InternalServerErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * {@link CompanyService} 인터페이스의 구현 클래스입니다.
 * 회사 등록(+Owner 생성), 조회, 수정, (비)활성화 등 회사 관련 비즈니스 로직을 담당합니다.
 * 모든 작업은 기본적으로 트랜잭션 내에서 수행됩니다.
 *
 * @see com.nhnacademy.company.service.CompanyService
 * @see com.nhnacademy.company.domain.Company
 * @see com.nhnacademy.company.repository.CompanyRepository
 * @see com.nhnacademy.company.repository.CompanyIndexRepository
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyIndexRepository companyIndexRepository;
    private final RoleRepository roleRepository;

    @Value("${app.security.owner-role-id:ROLE_OWNER}")
    private String ownerRoleId;

    /**
     * {@inheritDoc}
     * 이 구현체는 단일 트랜잭션 내에서 다음 작업을 순차적으로 수행합니다:
     * <ol>
     *     <li>요청된 회사 도메인의 중복 여부를 확인합니다. 중복 시 {@code AlreadyExistCompanyException} 발생.</li>
     *     <li>요청된 Owner 이메일의 시스템 전체 중복 여부를 확인합니다. 중복 시 {@code AlreadyExistMemberException} 발생.</li>
     *     <li>{@link Company#ofNewCompany(String, String, String, String, String)} (String, String, String, String, String)} (가정) 팩토리 메서드를 사용하여 {@code Company} 엔티티를 생성하고 저장합니다.</li>
     *     <li>설정된 {@code ownerRoleId}로 {@code Role} 엔티티를 조회합니다. 없으면 {@code NotExistRoleException} 발생.</li>
     *     <li>Owner 의 비밀번호를 PasswordUtil로 인코딩해서 저장합니다. </li>
     *     <li>{@link Member#ofNewMember(Company, Role, String, String)} 팩토리 메서드를 사용하여 Owner {@code Member} 엔티티를 생성하고 저장합니다.</li>
     * </ol>
     * 모든 과정이 성공하면 등록된 회사 정보를 담은 {@link CompanyResponse}를 반환합니다.
     */
    @Override
    public CompanyResponse registerCompany(CompanyRegisterRequest request) {
        log.debug("신규 회사 및 Owner 등록 요청 시작: 도메인 {}", request.getCompanyDomain());

        // 회사 도메인 중복 체크
        if (companyIndexRepository.existsByIndexAndFieldName(HashUtil.sha256Hex(request.getCompanyDomain()), "domain")) {
            log.warn("회사 등록 실패: 이미 존재하는 도메인 {}", request.getCompanyDomain());
            throw new AlreadyExistCompanyException("이미 사용 중인 회사 도메인입니다.");
        }
        //해쉬값 구하기.
        String domainHash = HashUtil.sha256Hex(request.getCompanyDomain());
        String nameHash = HashUtil.sha256Hex(request.getCompanyName());
        String emailHash = HashUtil.sha256Hex(request.getCompanyEmail());
        String mobileHash = HashUtil.sha256Hex(request.getCompanyMobile());
        String addressHash = HashUtil.sha256Hex(request.getCompanyAddress());

        // 개인정보 암호화
        String encryptedDomain = AESUtil.encrypt(request.getCompanyDomain());
        String encryptedName = AESUtil.encrypt(request.getCompanyName());
        String encryptedEmail = AESUtil.encrypt(request.getCompanyEmail());
        String encryptedMobile = AESUtil.encrypt(request.getCompanyMobile());
        String encryptedAddress = AESUtil.encrypt(request.getCompanyAddress());

        //인덱스 테이블에 추가
        List<CompanyIndex> indices = List.of(
                new CompanyIndex(domainHash, "domain", encryptedDomain),
                new CompanyIndex(nameHash, "name", encryptedName),
                new CompanyIndex(emailHash, "email", encryptedEmail),
                new CompanyIndex(mobileHash, "mobile", encryptedMobile),
                new CompanyIndex(addressHash, "address", encryptedAddress)
        );
        companyIndexRepository.saveAll(indices);
        log.debug("인덱스 테이블에 저장 완료");

        // 신규 회사 생성
        Company newCompany = Company.ofNewCompany(
                encryptedDomain,
                encryptedName,
                encryptedEmail,
                encryptedMobile,
                encryptedAddress
        );
        log.debug("신규 회사 생성: {}", newCompany);

        Company savedCompany = companyRepository.save(newCompany);
        log.debug("등록된 회사: {}", savedCompany);

        return mapToCompanyResponse(savedCompany);
    }

    /**
     * {@inheritDoc}
     * {@code @Transactional(readOnly = true)}로 설정되어 읽기 전용으로 실행됩니다.
     * 내부적으로 {@link #findCompanyByIdOrThrow(String)}를 호출하여 회사를 조회하며,
     * 해당 도메인의 회사가 없으면 {@code NotExistCompanyException}이 발생합니다.
     */
    @Override
    @Transactional(readOnly = true)
    public CompanyResponse getCompanyByDomain(String companyDomain) {
        log.debug("회사 정보 조회 요청: 도메인 {}", companyDomain);

        String hashDomain = HashUtil.sha256Hex(companyDomain);

        if(!companyIndexRepository.existsByIndex(hashDomain)) {
            throw new NotExistCompanyException("회사를 찾을 수 없습니다: 도메인 " + companyDomain);
        }
        Company company = findCompanyByIdOrThrow(companyDomain);
        log.debug("회사 정보 조회 성공: {}", company.getCompanyDomain());
        return mapToCompanyResponse(company);
    }


    /**
     * {@inheritDoc}
     * {@link #findCompanyByIdOrThrow(String)}를 통해 대상 회사를 조회한 후,
     * {@link Company#updateDetails(String, String, String)} (가정) 메서드를 호출하여 엔티티 상태를 변경합니다.
     * JPA 변경 감지(Dirty Checking)에 의해 데이터베이스 업데이트가 수행됩니다.
     * 대상 회사를 찾지 못하면 {@code NotExistCompanyException}이 발생합니다.
     */
    @Override
    public CompanyResponse updateCompany(String companyDomain, CompanyUpdateRequest request) {
        log.debug("회사 정보 수정 요청: 도메인 {}", companyDomain);
        Company company = findCompanyByIdOrThrow(companyDomain);
        company.updateDetails(
                AESUtil.encrypt(request.getCompanyName()),
                AESUtil.encrypt(request.getCompanyMobile()),
                AESUtil.encrypt(request.getCompanyAddress())
        );
        log.debug("회사 정보 수정 완료: 도메인 {}", companyDomain);

        return mapToCompanyResponse(company);
    }

    /**
     * {@inheritDoc}
     * {@link #findCompanyByIdOrThrow(String)}를 통해 대상 회사를 조회한 후,
     * {@link Company#updateDetails(String, String, String)} (가정) 메서드를 호출하여 엔티티 상태를 변경합니다.
     * JPA 변경 감지(Dirty Checking)에 의해 데이터베이스 업데이트가 수행됩니다.
     * 대상 회사를 찾지 못하면 {@code NotExistCompanyException}이 발생합니다.
     */
    @Override
    public CompanyResponse updateCompanyEmail(String companyDomain, CompanyUpdateEmailRequest request) {
        log.debug("회사 정보 수정 요청: 도메인 {}", companyDomain);
        Company company = findCompanyByIdOrThrow(companyDomain);
        company.updateEmail(AESUtil.encrypt(request.getNewEmail()));
        log.debug("회사 정보 수정 완료: 도메인 {}", companyDomain);

        return mapToCompanyResponse(company);
    }

    /**
     * {@inheritDoc}
     * {@link #findCompanyByIdOrThrow(String)}를 통해 대상 회사를 조회한 후,
     * {@link Company#deactivate()} 메서드를 호출하여 회사의 활성 상태 플래그를 {@code false}로 변경합니다.
     * JPA 변경 감지(Dirty Checking)에 의해 데이터베이스 업데이트가 수행됩니다.
     * 대상 회사를 찾지 못하면 {@code NotExistCompanyException}이 발생합니다.
     */
    @Override
    public void deactivateCompany(String companyDomain) {
        log.debug("회사 비활성화 요청: 도메인 {}", companyDomain);
        Company company = findCompanyByIdOrThrow(companyDomain);
        company.deactivate();
        log.info("회사 비활성화 완료: 도메인 {}", companyDomain);
    }

    /**
     * {@inheritDoc}
     * {@link #findCompanyByIdOrThrow(String)}를 통해 대상 회사를 조회한 후,
     * {@link Company#activate()} 메서드를 호출하여 회사의 활성 상태 플래그를 {@code true}로 변경합니다.
     * JPA 변경 감지(Dirty Checking)에 의해 데이터베이스 업데이트가 수행됩니다.
     * 대상 회사를 찾지 못하면 {@code NotExistCompanyException}이 발생합니다.
     */
    @Override
    public void activateCompany(String companyDomain) {
        log.debug("회사 활성화 요청: 도메인 {}", companyDomain);
        Company company = findCompanyByIdOrThrow(companyDomain);
        company.activate();
        log.info("회사 활성화 완료: 도메인 {}", companyDomain);
    }



    /**
     * {@inheritDoc}
     * {@code @Transactional(readOnly = true)}로 설정되어 읽기 전용으로 실행됩니다.
     * {@link CompanyRepository#findAll()}을 호출하여 모든 회사 엔티티를 조회한 후,
     * 각 엔티티를 {@link #mapToCompanyResponse(Company)}를 통해 {@link CompanyResponse} DTO로 변환하여 리스트로 반환합니다.
     */
    @Override
    @Transactional(readOnly = true)
    public List<CompanyResponse> getAllCompanies() {
        log.debug("모든 회사 목록 조회 요청");
        List<Company> companies = companyRepository.findAll();
        log.debug("총 {}개의 회사 조회됨.", companies.size());
        return companies.stream()
                .map(this::mapToCompanyResponse)
                .toList();
    }

    /**
     * 주어진 ID(도메인)로 Company를 조회하고, 없으면 NotExistCompanyException을 발생시키는 내부 헬퍼 메서드입니다.
     *
     * @param companyDomain 조회할 회사의 고유 도메인
     * @return 조회된 Company 엔티티
     * @throws NotExistCompanyException 해당 도메인의 회사가 존재하지 않을 경우
     */
    private Company findCompanyByIdOrThrow(String companyDomain) {
        if(companyDomain == null || companyDomain.isBlank()){
            throw new IllegalArgumentException("조회할려는 회사의 도메인 정보를 정확히 입력하세요.");
        }

        String hashKey = HashUtil.sha256Hex(companyDomain);
        CompanyIndex companyIndex = companyIndexRepository.findByIndex(hashKey).orElseThrow(
                () -> new NotExistCompanyException("회사를 찾을 수 없습니다: 도메인 " + companyDomain));

        return companyRepository.findById(companyIndex.getFieldValue())
                .orElseThrow(() -> {
                    log.warn("내부 조회 실패: 존재하지 않는 회사 도메인 {}", companyDomain);
                    return new NotExistCompanyException("회사를 찾을 수 없습니다: 도메인 " + companyDomain);
                });
    }

    /**
     * Company 엔티티를 디코딩하여 CompanyResponse DTO로 변환하는 내부 헬퍼 메서드입니다.
     * @param company 변환할 Company 엔티티
     * @return 변환된 CompanyResponse DTO
     */
    private CompanyResponse mapToCompanyResponse(Company company) {
        if(Objects.isNull(company)) {
            throw new IllegalArgumentException("DTO 변환 문제 발생!");
        }
        return new CompanyResponse(
                AESUtil.decrypt(company.getCompanyDomain()),
                AESUtil.decrypt(company.getCompanyName()),
                AESUtil.decrypt(company.getCompanyEmail()),
                AESUtil.decrypt(company.getCompanyMobile()),
                AESUtil.decrypt(company.getCompanyAddress()),
                company.getRegisteredAt(),
                company.isActive()
        );
    }
}
