package com.nhnacademy.member.repository.impl;

import com.nhnacademy.common.util.AESUtil;
import com.nhnacademy.common.util.HashUtil;
import com.nhnacademy.company.common.NotExistCompanyException;
import com.nhnacademy.company.domain.CompanyIndex;
import com.nhnacademy.company.domain.QCompany;
import com.nhnacademy.company.repository.CompanyIndexRepository;
import com.nhnacademy.member.dto.response.MemberResponse;
import com.nhnacademy.member.dto.response.QMemberResponse;
import com.nhnacademy.member.repository.CustomMemberRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import com.nhnacademy.member.domain.QMember;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class CustomMemberRepositoryImpl implements CustomMemberRepository {

    private final CompanyIndexRepository companyIndexRepository;

    private final JPAQueryFactory jpaQueryFactory;

    QMember qMember = QMember.member;
    QCompany qCompany = QCompany.company;

    @Override
    public Page<MemberResponse> findMembersFromCompanyDomain(String companyDomain, Pageable pageable, boolean isPending) {
        String hashValue = HashUtil.sha256Hex(companyDomain);
        CompanyIndex companyIndex = companyIndexRepository.findByHashValue(hashValue).orElseThrow(
                () -> new NotExistCompanyException("해당하는 값이 없습니다.")
        );

        BooleanBuilder whereClause = new BooleanBuilder();
        if(isPending){
            whereClause.and(qMember.role.roleId.eq("ROLE_PENDING"));
        }else {
            whereClause.and(qMember.role.roleId.ne("ROLE_PENDING"));
        }

        List<MemberResponse> memberResponseList = jpaQueryFactory.select(new QMemberResponse(
                qMember.memberNo,
                qMember.memberEmail,
                qMember.company.companyDomain,
                qMember.role.roleId,
                qMember.registeredAt,
                qMember.lastLoginAt
                ))
                .from(qMember)
                .join(qMember.company, qCompany)
                .where(qMember.company.companyDomain.eq(companyIndex.getCompanyDomain())
                        .and(whereClause))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<MemberResponse> decryptedList = memberResponseList.stream()
                .map(member -> MemberResponse.builder()
                        .memberNo(member.getMemberNo()) // 복호화할 필요 없는 값
                        .memberEmail(AESUtil.decrypt(member.getMemberEmail()))
                        .companyDomain(AESUtil.decrypt(member.getCompanyDomain()))
                        .roleId(member.getRoleId())
                        .registerAt(member.getRegisterAt())
                        .lastLoginAt(member.getLastLoginAt())
                        .build()).toList();

        Long listSize = jpaQueryFactory.select(qMember.count())
                .from(qMember)
                .join(qMember.company, qCompany)
                .where(qMember.company.companyDomain.eq(companyIndex.getCompanyDomain())
                        .and(whereClause))
                .fetchOne();
        if (Objects.isNull(listSize)){
            listSize = 0L;
        }

        return new PageImpl<>(decryptedList, pageable, listSize);
    }
}
