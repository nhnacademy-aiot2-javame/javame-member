package com.nhnacademy.company.service;

import com.nhnacademy.company.common.AlreadyExistCompanyException;
import com.nhnacademy.company.common.NotExistCompanyException;
import com.nhnacademy.company.common.NotFoundCompanyByEmailException;
import com.nhnacademy.company.domain.Company;
import com.nhnacademy.company.dto.request.CompanyRegisterRequest;
import com.nhnacademy.company.dto.request.CompanyUpdateEmailRequest;
import com.nhnacademy.company.dto.request.CompanyUpdateRequest;
import com.nhnacademy.company.dto.response.CompanyResponse;
import com.nhnacademy.company.repository.CompanyRepository;
import com.nhnacademy.company.service.impl.CompanyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock // CompanyRepository Mock 객체
    private CompanyRepository companyRepository;

    @InjectMocks // 테스트 대상 서비스 (CompanyRepository Mock 주입)
    private CompanyServiceImpl companyService; // 실제 구현 클래스명으로

    // 테스트에서 공통적으로 사용할 객체들 선언
    private Company companyA; // 테스트용 기본 회사 객체
    private CompanyRegisterRequest companyRegisterRequestA; // 회사 등록 요청 DTO
    private CompanyUpdateRequest companyUpdateRequestA;   // 회사 수정 요청 DTO

    @BeforeEach
    void setUp() {
        // 1. 테스트용 기본 Company 객체 생성 (DB 저장 아님, 단순 Java 객체)
        // Company의 PK는 String 타입인 companyDomain 이라고 가정
        companyA = Company.ofNewCompany(
                "nhnacademy.com",          // companyDomain (PK)
                "NHN Academy",             // companyName
                "contact@nhnacademy.com",  // companyEmail
                "031-123-4567",            // companyPhoneNumber
                "Bundang, Seongnam"        // companyAddress
        );

        // 2. 테스트용 DTO 객체 생성
        // CompanyRegisterRequest: 새로운 "NHN Academy" 회사를 등록하는 요청
        companyRegisterRequestA = new CompanyRegisterRequest(
                "nhnacademy.com",
                "NHN Academy",
                "contact@nhnacademy.com",
                "031-123-4567",
                "Bundang, Seongnam"
        );

        // CompanyUpdateRequest: "NHN Academy" 회사의 이름, 전화번호, 주소를 변경하는 요청
        // 도메인(ID)은 변경 불가하다고 가정
        companyUpdateRequestA = new CompanyUpdateRequest(
                "NHN Academy Corp.",       // newCompanyName
                "031-789-1234",            // newCompanyPhoneNumber
                "Pangyo, Seongnam"         // newCompanyAddress
        );
    }

    @Test
    @DisplayName("회사 등록 성공")
    void registerCompany_Success() {
        when(companyRepository.existsById(companyRegisterRequestA.getCompanyDomain())).thenReturn(false);

        ArgumentCaptor<Company> companyCaptor = ArgumentCaptor.forClass(Company.class);

        when(companyRepository.save(companyCaptor.capture())).thenAnswer(invocation -> {
            Company companyToSave = invocation.getArgument(0);

            ReflectionTestUtils.setField(companyToSave, "registeredAt", LocalDateTime.now()); // 값 직접 설정
            return companyToSave;
        });

        CompanyResponse companyResponse = companyService.registerCompany(companyRegisterRequestA);

        assertThat(companyResponse).isNotNull();
        assertThat(companyResponse.getCompanyDomain()).isEqualTo(companyRegisterRequestA.getCompanyDomain());
        assertThat(companyResponse.getCompanyName()).isEqualTo(companyRegisterRequestA.getCompanyName());
        assertThat(companyResponse.getCompanyEmail()).isEqualTo(companyRegisterRequestA.getCompanyEmail());
        assertThat(companyResponse.getCompanyMobile()).isEqualTo(companyRegisterRequestA.getCompanyMobile());
        assertThat(companyResponse.getCompanyAddress()).isEqualTo(companyRegisterRequestA.getCompanyAddress());
        assertThat(companyResponse.isActive()).isTrue();
        // 이제 이 검증이 통과해야 합니다.
        assertThat(companyResponse.getRegisteredAt()).isNotNull();

        Company savedCompany = companyCaptor.getValue();
        assertThat(savedCompany).isNotNull();
        assertThat(savedCompany.getCompanyDomain()).isEqualTo(companyRegisterRequestA.getCompanyDomain());
        assertThat(savedCompany.getCompanyName()).isEqualTo(companyRegisterRequestA.getCompanyName());
        assertThat(savedCompany.getCompanyEmail()).isEqualTo(companyRegisterRequestA.getCompanyEmail());
        assertThat(savedCompany.getCompanyMobile()).isEqualTo(companyRegisterRequestA.getCompanyMobile());
        assertThat(savedCompany.getCompanyAddress()).isEqualTo(companyRegisterRequestA.getCompanyAddress());
        assertThat(savedCompany.getCompanyDomain()).isEqualTo(companyRegisterRequestA.getCompanyDomain());

        assertThat(savedCompany.isActive()).isTrue();
        assertThat(savedCompany.getRegisteredAt()).isNotNull();

        verify(companyRepository, times(1)).existsById(companyRegisterRequestA.getCompanyDomain());
        verify(companyRepository, times(1)).save(any(Company.class));
    }

    @Test
    @DisplayName("회사 등록 실패 - 이미 존재하는 회사 도메인")
    void registerCompany_Fail_DomainAlreadyExists() {
        // given
        when(companyRepository.existsById(companyRegisterRequestA.getCompanyDomain())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> companyService.registerCompany(companyRegisterRequestA))
                .isInstanceOf(AlreadyExistCompanyException.class)
                .hasMessageContaining("이미 사용 중인 회사 도메인입니다.");

        // then (추가 검증)
        verify(companyRepository, times(1)).existsById(companyRegisterRequestA.getCompanyDomain());
        verify(companyRepository, never()).save(any(Company.class));
    }

    @Test
    @DisplayName("도메인으로 회사 조회 성공")
    void getCompanyByDomain_Success() {
        // given
        String existingDomain = companyA.getCompanyDomain();
        when(companyRepository.findById(existingDomain)).thenReturn(Optional.of(companyA));

        // when
        CompanyResponse foundCompanyResponse = companyService.getCompanyByDomain(existingDomain);

        // then
        assertThat(foundCompanyResponse).isNotNull();
        assertThat(foundCompanyResponse.getCompanyDomain()).isEqualTo(companyA.getCompanyDomain());
        assertThat(foundCompanyResponse.getCompanyName()).isEqualTo(companyA.getCompanyName());
        assertThat(foundCompanyResponse.getCompanyEmail()).isEqualTo(companyA.getCompanyEmail());
        assertThat(foundCompanyResponse.getCompanyMobile()).isEqualTo(companyA.getCompanyMobile());
        assertThat(foundCompanyResponse.getCompanyAddress()).isEqualTo(companyA.getCompanyAddress());
        assertThat(foundCompanyResponse.isActive()).isEqualTo(companyA.isActive());
        assertThat(foundCompanyResponse.getRegisteredAt()).isEqualTo(companyA.getRegisteredAt());

        verify(companyRepository, times(1)).findById(existingDomain);
    }

    @Test
    @DisplayName("도메인으로 회사 조회 실패 - 존재하지 않는 도메인")
    void getCompanyByDomain_Fail_DomainNotFound() {
        // given
        String nonExistingDomain = "nonexisting.com";
        when(companyRepository.findById(nonExistingDomain)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> companyService.getCompanyByDomain(nonExistingDomain))
                .isInstanceOf(NotExistCompanyException.class)
                .hasMessageContaining("회사를 찾을 수 없습니다: 도메인 " + nonExistingDomain);

        // then (추가 검증)
        verify(companyRepository, times(1)).findById(nonExistingDomain);
    }

    @Test
    @DisplayName("회사 정보 수정 성공")
    void updateCompany_Success() {
        // given - Mock 설정 및 테스트 데이터 준비
        String existingDomain = companyA.getCompanyDomain(); // 수정 대상 회사 도메인
        // companyUpdateRequestA는 @BeforeEach setUp()에서 이미 준비됨 (새로운 이름, 전화번호, 주소)

        // findById가 호출되면 수정 대상 companyA 객체를 Optional에 담아 반환
        // Company 객체를 spy로 감싸서 updateDetails 메서드 호출을 검증할 수 있도록 함
        Company spiedCompanyA = spy(companyA);
        when(companyRepository.findById(existingDomain)).thenReturn(Optional.of(spiedCompanyA));

        // when - 서비스 메서드 호출
        CompanyResponse updatedCompanyResponse = companyService.updateCompany(existingDomain, companyUpdateRequestA);

        // then - 결과 검증
        // 1. 반환된 CompanyResponse가 null이 아닌지 확인
        assertThat(updatedCompanyResponse).isNotNull();
        // 2. 반환된 CompanyResponse의 내용이 companyUpdateRequestA의 내용과 일치하는지 확인
        assertThat(updatedCompanyResponse.getCompanyDomain()).isEqualTo(existingDomain); // 도메인은 변경되지 않음
        assertThat(updatedCompanyResponse.getCompanyName()).isEqualTo(companyUpdateRequestA.getCompanyName());
        assertThat(updatedCompanyResponse.getCompanyMobile()).isEqualTo(companyUpdateRequestA.getCompanyMobile());
        assertThat(updatedCompanyResponse.getCompanyAddress()).isEqualTo(companyUpdateRequestA.getCompanyAddress());
        // Email, isActive, registeredAt은 이 메서드에서 변경되지 않는다고 가정
        assertThat(updatedCompanyResponse.getCompanyEmail()).isEqualTo(companyA.getCompanyEmail());
        assertThat(updatedCompanyResponse.isActive()).isEqualTo(companyA.isActive());
        assertThat(updatedCompanyResponse.getRegisteredAt()).isEqualTo(companyA.getRegisteredAt());


        // 3. spiedCompanyA 객체의 updateDetails 메서드가 올바른 인자로 호출되었는지 검증
        verify(spiedCompanyA, times(1)).updateDetails(
                companyUpdateRequestA.getCompanyName(),
                companyUpdateRequestA.getCompanyMobile(),
                companyUpdateRequestA.getCompanyAddress()
        );
        // 4. JPA 변경 감지로 동작하므로, companyRepository.save()는 호출되지 않음을 확인 (선택적)
        verify(companyRepository, never()).save(any(Company.class));
        // 5. findById는 1번 호출됨
        verify(companyRepository, times(1)).findById(existingDomain);
    }

    @Test
    @DisplayName("회사 정보 수정 실패 - 존재하지 않는 회사 도메인")
    void updateCompany_Fail_DomainNotFound() {
        // given - Mock 설정
        String nonExistingDomain = "nonexisting.com";
        // companyUpdateRequestA는 @BeforeEach setUp()에서 이미 준비됨
        when(companyRepository.findById(nonExistingDomain)).thenReturn(Optional.empty());

        // when & then - 예외 발생 검증
        assertThatThrownBy(() -> companyService.updateCompany(nonExistingDomain, companyUpdateRequestA))
                .isInstanceOf(NotExistCompanyException.class)
                .hasMessageContaining("회사를 찾을 수 없습니다: 도메인 " + nonExistingDomain);

        // then (추가 검증)
        verify(companyRepository, times(1)).findById(nonExistingDomain);
        verify(companyRepository, never()).save(any(Company.class));
    }

    @Test
    @DisplayName("회사 이메일 수정 성공")
    void updateCompanyEmail_Success() {
        // given - 테스트 데이터 및 Mock 설정
        String existingDomain = companyA.getCompanyDomain();
        String currentEmail = companyA.getCompanyEmail(); // 현재 회사 이메일
        String newEmail = "new_contact@nhnacademy.com";   // 변경할 새 이메일

        CompanyUpdateEmailRequest updateEmailRequest = new CompanyUpdateEmailRequest(currentEmail, newEmail);

        // 1. 현재 이메일로 회사가 존재하는지 확인: existsByCompanyEmail(currentEmail) -> true
        when(companyRepository.existsByCompanyEmail(currentEmail)).thenReturn(true);

        // 2. 도메인으로 회사 조회: findById(existingDomain) -> companyA 반환 (spy로 감싸서 메서드 호출 검증)
        Company spiedCompanyA = spy(companyA);
        when(companyRepository.findById(existingDomain)).thenReturn(Optional.of(spiedCompanyA));

        // when - 서비스 메서드 호출
        CompanyResponse updatedCompanyResponse = companyService.updateCompanyEmail(existingDomain, updateEmailRequest);

        // then - 결과 검증
        assertThat(updatedCompanyResponse).isNotNull();
        assertThat(updatedCompanyResponse.getCompanyDomain()).isEqualTo(existingDomain);
        assertThat(updatedCompanyResponse.getCompanyEmail()).isEqualTo(newEmail); // 이메일 변경 확인
        // 다른 필드들은 변경되지 않았는지 확인 (예: 회사 이름)
        assertThat(updatedCompanyResponse.getCompanyName()).isEqualTo(companyA.getCompanyName());

        // Company 객체의 updateEmail 메서드가 올바른 인자로 호출되었는지 검증
        verify(spiedCompanyA, times(1)).updateEmail(newEmail);
        // Mock Repository 메서드 호출 검증
        verify(companyRepository, times(1)).existsByCompanyEmail(currentEmail);
        verify(companyRepository, times(1)).findById(existingDomain);
        verify(companyRepository, never()).save(any(Company.class)); // 변경 감지로 동작
    }

    @Test
    @DisplayName("회사 이메일 수정 실패 - 현재 이메일로 회사를 찾을 수 없음")
    void updateCompanyEmail_Fail_CurrentEmailNotFound() {
        // given
        String existingDomain = companyA.getCompanyDomain();
        String nonExistingCurrentEmail = "non_existing_current@email.com";
        String newEmail = "new@email.com";

        CompanyUpdateEmailRequest updateEmailRequest = new CompanyUpdateEmailRequest(nonExistingCurrentEmail, newEmail);

        // 현재 이메일로 회사가 존재하지 않음: existsByCompanyEmail(nonExistingCurrentEmail) -> false
        when(companyRepository.existsByCompanyEmail(nonExistingCurrentEmail)).thenReturn(false);

        // when & then - 예외 발생 검증
        assertThatThrownBy(() -> companyService.updateCompanyEmail(existingDomain, updateEmailRequest))
                .isInstanceOf(NotFoundCompanyByEmailException.class)
                .hasMessageContaining(nonExistingCurrentEmail); // 예외 메시지 확인 (서비스 코드 참고)

        // Mock Repository 메서드 호출 검증
        verify(companyRepository, times(1)).existsByCompanyEmail(nonExistingCurrentEmail);
        verify(companyRepository, never()).findById(anyString()); // findById는 호출되지 않아야 함
        verify(companyRepository, never()).save(any(Company.class));
    }

    @Test
    @DisplayName("회사 이메일 수정 실패 - 존재하지 않는 회사 도메인")
    void updateCompanyEmail_Fail_CompanyDomainNotFound() {
        // given
        String nonExistingDomain = "nonexisting.com";
        String currentEmail = companyA.getCompanyEmail(); // 현재 이메일은 존재한다고 가정
        String newEmail = "new@email.com";

        CompanyUpdateEmailRequest updateEmailRequest = new CompanyUpdateEmailRequest(currentEmail, newEmail);

        // 현재 이메일로 회사는 존재함: existsByCompanyEmail(currentEmail) -> true
        when(companyRepository.existsByCompanyEmail(currentEmail)).thenReturn(true);
        // 하지만 업데이트 대상 회사 도메인이 존재하지 않음: findById(nonExistingDomain) -> Optional.empty()
        when(companyRepository.findById(nonExistingDomain)).thenReturn(Optional.empty());

        // when & then - 예외 발생 검증
        assertThatThrownBy(() -> companyService.updateCompanyEmail(nonExistingDomain, updateEmailRequest))
                .isInstanceOf(NotExistCompanyException.class) // findCompanyByIdOrThrow 내부에서 발생
                .hasMessageContaining("회사를 찾을 수 없습니다: 도메인 " + nonExistingDomain);

        // Mock Repository 메서드 호출 검증
        verify(companyRepository, times(1)).existsByCompanyEmail(currentEmail);
        verify(companyRepository, times(1)).findById(nonExistingDomain);
        verify(companyRepository, never()).save(any(Company.class));
    }


    @Test
    @DisplayName("회사 비활성화 성공")
    void deactivateCompany_Success() {
        // given
        String existingDomain = companyA.getCompanyDomain();
        // 초기 상태는 active=true 라고 가정 (setUp에서 설정됨)
        Company spiedCompanyA = spy(companyA); // companyA는 active=true 상태
        when(companyRepository.findById(existingDomain)).thenReturn(Optional.of(spiedCompanyA));

        // when
        companyService.deactivateCompany(existingDomain);

        // then
        verify(spiedCompanyA, times(1)).deactivate(); // Company의 deactivate 메서드 호출 검증
        // 실제 spiedCompanyA 객체의 active 상태가 false로 변경되었는지 확인 가능 (spy는 실제 객체 기반)
        assertThat(spiedCompanyA.isActive()).isFalse();
        verify(companyRepository, times(1)).findById(existingDomain);
        verify(companyRepository, never()).save(any(Company.class)); // 변경 감지로 동작
    }

    @Test
    @DisplayName("회사 비활성화 실패 - 존재하지 않는 회사 도메인")
    void deactivateCompany_Fail_DomainNotFound() {
        // given
        String nonExistingDomain = "nonexisting.com";
        when(companyRepository.findById(nonExistingDomain)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> companyService.deactivateCompany(nonExistingDomain))
                .isInstanceOf(NotExistCompanyException.class)
                .hasMessageContaining("회사를 찾을 수 없습니다: 도메인 " + nonExistingDomain);

        verify(companyRepository, times(1)).findById(nonExistingDomain);
    }

    @Test
    @DisplayName("회사 활성화 성공")
    void activateCompany_Success() {
        // given
        String existingDomain = companyA.getCompanyDomain();
        // 테스트를 위해 초기 상태를 비활성으로 만듦
        companyA.deactivate(); // companyA는 이제 active=false 상태
        assertThat(companyA.isActive()).isFalse(); // 초기 상태 확인

        Company spiedCompanyA = spy(companyA);
        when(companyRepository.findById(existingDomain)).thenReturn(Optional.of(spiedCompanyA));

        // when
        companyService.activateCompany(existingDomain);

        // then
        verify(spiedCompanyA, times(1)).activate(); // Company의 activate 메서드 호출 검증
        assertThat(spiedCompanyA.isActive()).isTrue(); // 상태가 true로 변경되었는지 확인
        verify(companyRepository, times(1)).findById(existingDomain);
        verify(companyRepository, never()).save(any(Company.class));
    }

    @Test
    @DisplayName("회사 활성화 실패 - 존재하지 않는 회사 도메인")
    void activateCompany_Fail_DomainNotFound() {
        // given
        String nonExistingDomain = "nonexisting.com";
        when(companyRepository.findById(nonExistingDomain)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> companyService.activateCompany(nonExistingDomain))
                .isInstanceOf(NotExistCompanyException.class)
                .hasMessageContaining("회사를 찾을 수 없습니다: 도메인 " + nonExistingDomain);

        verify(companyRepository, times(1)).findById(nonExistingDomain);
    }

    @Test
    @DisplayName("모든 회사 조회 성공 - 회사 목록 반환")
    void getAllCompanies_Success_ReturnListOfCompanies() {
        // given
        Company companyB = Company.ofNewCompany("google.com", "Google Inc.", "contact@google.com", "111-222-3333", "Mountain View");
        when(companyRepository.findAll()).thenReturn(List.of(companyA, companyB));

        // when
        List<CompanyResponse> companyResponses = companyService.getAllCompanies();

        // then
        assertThat(companyResponses).isNotNull();
        assertThat(companyResponses).hasSize(2);
        assertThat(companyResponses).extracting(CompanyResponse::getCompanyDomain)
                .containsExactlyInAnyOrder(companyA.getCompanyDomain(), companyB.getCompanyDomain());
        // 필요시 다른 필드들도 검증

        verify(companyRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("모든 회사 조회 성공 - 회사 없음")
    void getAllCompanies_Success_ReturnEmptyListWhenNoCompanies() {
        // given
        when(companyRepository.findAll()).thenReturn(Collections.emptyList());

        // when
        List<CompanyResponse> companyResponses = companyService.getAllCompanies();

        // then
        assertThat(companyResponses).isNotNull();
        assertThat(companyResponses).isEmpty();

        verify(companyRepository, times(1)).findAll();
    }
}