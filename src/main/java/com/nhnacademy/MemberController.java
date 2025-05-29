package com.nhnacademy;


import com.nhnacademy.common.annotation.HasRole;
import com.nhnacademy.member.dto.request.MemberPasswordChangeRequest;
import com.nhnacademy.member.dto.request.MemberRegisterRequest;
import com.nhnacademy.member.dto.response.MemberLoginResponse;
import com.nhnacademy.member.dto.response.MemberResponse;
import com.nhnacademy.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


/**
 * 회원(Member) 관련 HTTP 요청을 처리하는 REST 컨트롤러입니다.
 * 회원 가입, 조회, 수정, 탈퇴, 로그인 정보 제공 등의 API 엔드포인트를 제공합니다.
 * 모든 경로는 "/members"를 기본으로 합니다.
 */
@RestController
@RequestMapping(value = "/members", produces = MediaType.APPLICATION_JSON_VALUE)// 기본 경로 및 JSON 형태 응답 타입 설정
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 신규 회원을 등록합니다 (기존 회사에 User 역할로 가입).
     * 요청 본문에는 회원 정보(이메일, 비밀번호, 이름)와 소속될 회사 도메인이 포함되어야 합니다.
     * 성공 시 HTTP 상태 코드 201 (Created)과 생성된 회원 정보를 반환합니다.
     *
     * @param request 회원 가입 정보 DTO ({@link MemberRegisterRequest})
     * @return 생성된 회원 정보 ({@link MemberResponse})와 상태 코드 201
     */
    @PostMapping("/register")
    public ResponseEntity<MemberResponse> registerMember(@Validated @RequestBody MemberRegisterRequest request) {
        MemberResponse response = memberService.registerMember(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("register/owners")
    public ResponseEntity<MemberResponse> registerOwner(@Validated @RequestBody MemberRegisterRequest request) {
        MemberResponse response = memberService.registerOwner(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 회원이 로그인하고 가장 최근에 로그인한 시간을 나타내는 lastLoginAt을 업데이트합니다.
     * 내부 서비스 전용으로 사용됩니다.
     * @param email 업데이트할 대상의 이메일 정보.
     * @return HTTP 상태 코드 200 (OK)를 반환합니다.
     */
    @PutMapping("/internal/last-login")
    public ResponseEntity<Void> updateLastLogin(@RequestParam String email){
        memberService.updateLoginAt(email);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 주어진 회원 ID(UUID)에 해당하는 회원 정보를 조회합니다.
     * 성공 시 HTTP 상태 코드 200 (OK)과 회원 정보를 반환합니다.
     *
     * @param memberNo 조회할 회원의 UUID (경로 변수)
     * @return 조회된 회원 정보 ({@link MemberResponse})와 상태 코드 200
     */
    @GetMapping("/{memberNo}")
    public ResponseEntity<MemberResponse> getMemberById(@PathVariable Long memberNo) {
        MemberResponse response = memberService.getMemberById(memberNo);
        return ResponseEntity.ok(response);
    }

    /**
     * 주어진 회원 email 에 해당하는 회원 정보를 조회합니다.
     * 성공 시 HTTP 상태 코드 200 (OK)과 회원 정보를 반환합니다.
     *
     * @param userEmail 토큰에서 얻은 유저의 이메일 정보입니다.
     * @return 조회된 회원 정보 ({@link MemberResponse})와 상태 코드 200
     */
    @GetMapping("/me")
    public ResponseEntity<MemberResponse> getMemberByEmail(@RequestHeader("X-User-Email")String userEmail) {
        if(userEmail == null || userEmail.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다. ");
        }
        MemberResponse response = memberService.getMemberByEmail(userEmail);
        return ResponseEntity.ok(response);
    }


    /**
     * 주어진 회원 ID에 해당하는 회원의 비밀번호를 변경합니다.
     * 요청 본문에는 현재 비밀번호와 새 비밀번호가 포함되어야 합니다.
     * 성공 시 HTTP 상태 코드 204 (No Content)를 반환합니다. (본문 없음)
     *
     * @param memberNo 비밀번호를 변경할 회원의 UUID (경로 변수)
     * @param request  비밀번호 변경 정보 DTO ({@link MemberPasswordChangeRequest})
     * @return 상태 코드 204 (No Content)
     */
    @PutMapping("/{memberNo}/password")
    public ResponseEntity<Void> changeMemberPassword(
            @PathVariable Long memberNo,
            @Validated @RequestBody MemberPasswordChangeRequest request,
            @RequestHeader("X-User-Email")String userEmail) {

        MemberResponse memberResponse = memberService.getMemberByEmail(userEmail);
        if(!memberResponse.getMemberNo().equals(memberNo)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "접근 권한이 없습니다. ");
        }
        memberService.changeMemberPassword(memberNo, request);
        return ResponseEntity.noContent().build();
    }

    /**
     * 주어진 회원 ID에 해당하는 회원을 탈퇴 처리(논리적 삭제)합니다.
     * 성공 시 HTTP 상태 코드 204 (No Content)를 반환합니다. (본문 없음)
     *
     * @param memberNo 탈퇴 처리할 회원의 UUID (경로 변수)
     * @return 상태 코드 204 (No Content)
     */
    @DeleteMapping("/{memberNo}")
    public ResponseEntity<Void> deleteMember(
            @PathVariable Long memberNo,
            @RequestHeader("X-User-Email")String userEmail) {

        MemberResponse memberResponse = memberService.getMemberByEmail(userEmail);
        if(!memberResponse.getMemberNo().equals(memberNo)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 정보를 삭제할 권한이 없습니다.");
        }
        memberService.deleteMember(memberNo);
        return ResponseEntity.noContent().build();
    }

    /**
     * 주어진 이메일 주소에 해당하는 회원의 로그인 관련 정보를 조회합니다.
     * 이 API는 주로 다른 내부 서비스(예: 인증 서버)에서 호출됩니다.
     * 성공 시 HTTP 상태 코드 200 (OK)와 로그인 정보를 반환합니다.
     *
     * @param userEmail 조회할 회원의 이메일 주소 (경로 변수)
     * @return 로그인 관련 정보 ({@link MemberLoginResponse})와 상태 코드 200
     */
    @GetMapping("/me/login-info")
    public ResponseEntity<MemberLoginResponse> getLoginInfoByEmail(
            @RequestHeader("X-User-Email")String userEmail) {

        if(userEmail == null || userEmail.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 정보에 대한 접근이 불가합니다. ");
        }
        MemberLoginResponse response = memberService.getLoginInfoByEmail(userEmail);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/companies/{company-domain}")
    public ResponseEntity<Page<MemberResponse>> getMemberResponseFromCompanyDomain
            (@PathVariable("company-domain") String companyDomain, @PageableDefault(size=10) Pageable pageable, @RequestParam("isPending")boolean isPending) {
        Page<MemberResponse> memberResponsePage = memberService.getMemberResponseFromCompanyDomain(companyDomain, pageable, isPending);
        return ResponseEntity.ok(memberResponsePage);
    }

    @PutMapping("/role/{member-no}")
    public ResponseEntity<String> memberRoleUpdate(@PathVariable("member-no")Long memberNo, @RequestParam("role")String role){
        String updateRole = memberService.updateMemberRole(memberNo, role);
        return ResponseEntity.ok(updateRole);
    }
}
