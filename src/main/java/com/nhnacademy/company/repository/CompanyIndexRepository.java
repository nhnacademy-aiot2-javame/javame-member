package com.nhnacademy.company.repository;

import com.nhnacademy.company.domain.CompanyIndex;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyIndexRepository extends JpaRepository<CompanyIndex, String> {

    /**
     * @param index 해쉬 값
     * @return boolean 값
     */
    boolean existsByIndex(String index);

    /**
     * @param index 해쉬 값
     * @param fieldName 필드명
     * @return boolean 값
     */
    boolean existsByIndexAndFieldName(String index, String fieldName);

    /**
     * @param index 해쉬값
     * @return Optional<CompanyIndex>
     */
    Optional<CompanyIndex> findByIndex(String index);
}
