package com.nhnacademy.company.repository;

import com.nhnacademy.company.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 회사(Company) 엔티티에 대한 데이터 액세스 작업을 위한 리포지토리 인터페이스입니다.
 * Spring Data JPA의 JpaRepository를 상속받아 기본적인 CRUD 기능을 제공받습니다.
 * 기본키는 회사 도메인(String)입니다.
 */
public interface CompanyRepository extends JpaRepository<Company, String> {

    /**
     *
     * @param companyEmail 회사 대표 이메일
     * @return boolean값
     */
    boolean existsByCompanyEmail(String companyEmail);

    /**
     * 회사명으로 회사를 조회합니다.
     *
     * @param companyName 회사명
     * @return 회사 엔티티
     */
    Optional<Company> findByCompanyName(String companyName);

    /**
     * 주어진 회사 이름이 존재하는지 확인합니다.
     *
     * @param companyName 확인할 회사의 이름
     * @return 존재하면 true, 그렇지 않으면 false
     */
    boolean existsByCompanyName(String companyName);
}
