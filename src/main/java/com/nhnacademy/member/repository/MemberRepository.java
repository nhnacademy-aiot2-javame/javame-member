package com.nhnacademy.member.repository;

import com.nhnacademy.company.domain.Company;
import com.nhnacademy.member.domain.Member;
import com.nhnacademy.role.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 회원(Member) 엔티티에 대한 데이터 액세스 작업을 위한 리포지토리 인터페이스입니다.
 * Spring Data JPA의 JpaRepository를 상속받아 기본적인 CRUD 기능을 제공받습니다.
 */
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * 주어진 이메일 주소로 회원을 조회합니다.
     * 이메일은 고유해야 하므로 결과는 최대 1개입니다.
     *
     * @param memberEmail 조회할 회원의 이메일 주소
     * @return 해당 이메일을 가진 회원 정보 (Optional)
     */

    Optional<Member> findByMemberEmail(String memberEmail);

    /**
     * 주어진 이메일 주소를 가진 회원이 존재하는지 확인합니다.
     *
     * @param memberEmail 확인할 회원의 이메일 주소
     * @return 존재하면 true, 그렇지 않으면 false
     */
    boolean existsByMemberEmail(String memberEmail);

    /**
     * 특정 회사에 소속된 모든 회원을 조회합니다.
     *
     * @param company 조회할 회사 엔티티
     * @return 해당 회사에 소속된 회원 목록
     */
    List<Member> findByCompany(Company company);

    /**
     * 특정 회사에 소속되고 특정 역할을 가진 모든 회원을 조회합니다.
     *
     * @param company 조회할 회사 엔티티
     * @param role    조회할 역할 엔티티
     * @return 해당 회사와 역할을 가진 회원 목록
     */
    List<Member> findByCompanyAndRole(Company company, Role role);
}
