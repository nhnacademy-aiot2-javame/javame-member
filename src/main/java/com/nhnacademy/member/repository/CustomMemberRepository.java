package com.nhnacademy.member.repository;

import com.nhnacademy.member.dto.response.MemberResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomMemberRepository {

    Page<MemberResponse> findMembersFromCompanyDomain(String companyDomain, Pageable pageable, boolean isPending);

}
