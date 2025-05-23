package com.nhnacademy.company.repository;

import com.nhnacademy.company.domain.Company;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@DataJpaTest
class CompanyRepositoryTest {

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    TestEntityManager testEntityManager;

    private Company companyA;
    private Company companyB_SameName; // companyA와 같은 이름
    private Company companyC_UniqueName;

    @BeforeEach
    void setUp() {
        // 테스트용 회사 3개 생성 (A와 B는 이름이 같음)
        companyA = Company.ofNewCompany(
                "javame.com",          // PK (도메인)
                "NHN Academy",         // 회사 이름
                "hr@javame.com",       // 대표 이메일
                "010-1111-1111",
                "주소 A"
        );
        companyB_SameName = Company.ofNewCompany(
                "example.org",         // PK (도메인) - 달라야 함
                "NHN Academy",         // 회사 이름 - companyA와 동일
                "info@example.org",    // 대표 이메일 - 달라야 함 (unique 가정)
                "010-2222-2222",
                "주소 B"
        );
        companyC_UniqueName = Company.ofNewCompany(
                "unique.dev",          // PK (도메인)
                "Unique Corp",         // 회사 이름 - 고유
                "contact@unique.dev",  // 대표 이메일 - 달라야 함 (unique 가정)
                "010-3333-3333",
                "주소 C"
        );

        // DB에 저장
        testEntityManager.persist(companyA);
        testEntityManager.persist(companyB_SameName);
        testEntityManager.persist(companyC_UniqueName);
        testEntityManager.flush();
    }

    @Test
    @DisplayName("회사 저장 및 ID(도메인)로 조회")
    void saveAndFindById_ShouldWork() {
        // given: setUp에서 저장됨

        // when: ID로 조회
        Optional<Company> foundA = companyRepository.findById(companyA.getCompanyDomain());
        Optional<Company> foundB = companyRepository.findById(companyB_SameName.getCompanyDomain());
        Optional<Company> foundC = companyRepository.findById(companyC_UniqueName.getCompanyDomain());

        // then: 조회 성공 및 내용 확인
        assertThat(foundA).isPresent();
        assertThat(foundA.get().getCompanyName()).isEqualTo("NHN Academy");

        assertThat(foundB).isPresent();
        assertThat(foundB.get().getCompanyName()).isEqualTo("NHN Academy");

        assertThat(foundC).isPresent();
        assertThat(foundC.get().getCompanyName()).isEqualTo("Unique Corp");
    }

    @Test
    @DisplayName("존재하지 않는 ID(도메인)로 조회 시 Optional.empty 반환")
    void findById_WhenNotExists_ShouldReturnEmpty() {
        // when
        Optional<Company> notFound = companyRepository.findById("nonexistent.com");

        // then
        assertThat(notFound).isNotPresent();
    }

    @Test
    @DisplayName("전체 회사 조회")
    void findAll_ShouldReturnAllCompanies() {
        // when
        List<Company> allCompanies = companyRepository.findAll();

        // then: setUp에서 저장한 3개 회사 모두 조회 확인
        assertThat(allCompanies).hasSize(3);
        assertThat(allCompanies).extracting(Company::getCompanyDomain)
                .containsExactlyInAnyOrder(
                        companyA.getCompanyDomain(),
                        companyB_SameName.getCompanyDomain(),
                        companyC_UniqueName.getCompanyDomain()
                );
    }

    @Test
    @DisplayName("회사 정보 업데이트")
    void updateCompany_ShouldModifyDetails() {
        // given: companyC 조회
        Company companyToUpdate = companyRepository.findById(companyC_UniqueName.getCompanyDomain()).orElseThrow();
        String newName = "Updated Unique Corp";
        String newEmail = "updated@unique.dev";
        String newMobile = "010-4444-4444";
        String newAddress = "주소 D";

        // when: 정보 수정 및 flush (변경 감지)
        companyToUpdate.updateDetails(newName, newMobile, newAddress);
        companyToUpdate.updateEmail(newEmail);
        companyRepository.flush();

        // then: 다시 조회하여 변경 확인
        Company updatedCompany = testEntityManager.find(Company.class, companyC_UniqueName.getCompanyDomain());
        assertThat(updatedCompany.getCompanyName()).isEqualTo(newName);
        assertThat(updatedCompany.getCompanyEmail()).isEqualTo(newEmail);
        assertThat(updatedCompany.getCompanyMobile()).isEqualTo(newMobile);
        assertThat(updatedCompany.getCompanyAddress()).isEqualTo(newAddress);
        assertThat(updatedCompany.getRegisteredAt()).isEqualTo(companyC_UniqueName.getRegisteredAt());
        assertThat(updatedCompany.isActive()).isEqualTo(companyC_UniqueName.isActive());
    }

    @Test
    @DisplayName("회사 삭제")
    void deleteCompany_ShouldRemove() {
        // given: companyA 조회

        // when: 삭제 및 flush
        companyRepository.delete(companyA);
        companyRepository.flush();

        // then: 삭제 후 조회 안됨 확인
        Optional<Company> deletedCompany = companyRepository.findById(companyA.getCompanyDomain());
        assertThat(deletedCompany).isNotPresent();
    }

    @Test
    @DisplayName("중복된 ID(도메인)로 회사 저장 시 예외 발생")
    void save_DuplicateId_ShouldThrowException() {
        // given: 기존 ID와 동일한 ID를 가진 새 회사 객체
        Company duplicateCompany = Company.ofNewCompany(
                companyA.getCompanyDomain(), // <- 중복 ID
                "Duplicate Name",
                "duplicate@email.com",
                "010-4444-4444",
                "주소 D"
        );

        // when & then: persist 시 PK 중복 예외 확인
        assertThatThrownBy(() -> {
            testEntityManager.persist(duplicateCompany);
            testEntityManager.flush();
        }).isInstanceOf(PersistenceException.class);
    }

    @Test
    @DisplayName("회사 이메일 존재 여부 확인")
    void existsByCompanyEmail_ShouldReturnCorrectBoolean() {
        // when & then
        assertThat(companyRepository.existsByCompanyEmail(companyA.getCompanyEmail())).isTrue();
        assertThat(companyRepository.existsByCompanyEmail(companyB_SameName.getCompanyEmail())).isTrue();
        assertThat(companyRepository.existsByCompanyEmail(companyC_UniqueName.getCompanyEmail())).isTrue();
        assertThat(companyRepository.existsByCompanyEmail("nonexistent@email.com")).isFalse();
    }

    @Test
    @DisplayName("회사 이름으로 회사 목록 조회")
    void findByCompanyName_ShouldReturnListOfCompanies() {
        // when: 이름으로 조회
        Optional<Company> foundByNameC = companyRepository.findByCompanyName("Unique Corp");
        Optional<Company> foundByNonExistentName = companyRepository.findByCompanyName("NonExistent Name");

        // "Unique Corp" 이름의 회사가 존재해야 함
        assertThat(foundByNameC).isPresent();
        assertThat(foundByNameC.get().getCompanyDomain()).isEqualTo(companyC_UniqueName.getCompanyDomain());

        // 존재하지 않는 이름일 경우 Optional.empty 여야 함
        assertThat(foundByNonExistentName).isNotPresent();
    }

    @Test
    @DisplayName("회사 이름 존재 여부 확인")
    void existsByCompanyName_ShouldReturnCorrectBoolean() {
        // when & then
        assertThat(companyRepository.existsByCompanyName("NHN Academy")).isTrue();
        assertThat(companyRepository.existsByCompanyName("Unique Corp")).isTrue();
        assertThat(companyRepository.existsByCompanyName("NonExistent Name")).isFalse();
    }
}