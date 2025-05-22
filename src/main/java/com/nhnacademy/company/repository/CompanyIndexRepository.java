package com.nhnacademy.company.repository;

import com.nhnacademy.company.domain.CompanyIndex;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyIndexRepository extends JpaRepository<CompanyIndex, Long> {

    /**
     * @param hashValue 해쉬 값
     * @return boolean 값
     */
    boolean existsByHashValue(String hashValue);

    /**
     * @param hashValue 해쉬 값
     * @param fieldName 필드명
     * @return boolean 값
     */
    boolean existsByHashValueAndFieldName(String hashValue, String fieldName);

    /**
     * @param hashValue 해쉬값
     * @return Optional<CompanyIndex>
     */
    Optional<CompanyIndex> findByHashValue(String hashValue);
}
