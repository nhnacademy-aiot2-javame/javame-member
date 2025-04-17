package com.nhnacademy.member.controller;

import com.nhnacademy.member.dto.request.MemberRegisterRequest;
import com.nhnacademy.member.dto.response.MemberResponse;
import com.nhnacademy.member.dto.request.MemberUpdateRequest;
import com.nhnacademy.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public ResponseEntity<MemberResponse> registerMember(@Validated @RequestBody MemberRegisterRequest memberRegisterRequest){
        MemberResponse memberResponse = memberService.registerMember(memberRegisterRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(memberResponse);
    }

    @GetMapping("/{member-id}")
    public ResponseEntity<MemberResponse> getMember(@PathVariable("member-id") String memberId){
        MemberResponse memberResponse = memberService.getMemberByMemberId(memberId);
        return ResponseEntity.ok(memberResponse);
    }


    @PutMapping("/{member-id}")
    public ResponseEntity<MemberResponse> updateMember(@Validated @RequestBody MemberUpdateRequest memberUpdateRequest
            ,@PathVariable("member-id") String memberId){
        MemberResponse memberResponse = memberService.updateMember(memberId, memberUpdateRequest);
        return ResponseEntity
                .ok(memberResponse);
    }

    @DeleteMapping("/{member-id}")
    public ResponseEntity<Void> deleteMember(@PathVariable("member-id") String memberId ){
        memberService.deleteMember(memberId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

}
