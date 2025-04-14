package com.nhnacademy.exam.javamememberapi.member.controller;

import com.nhnacademy.exam.javamememberapi.member.dto.MemberRegisterRequest;
import com.nhnacademy.exam.javamememberapi.member.dto.MemberResponse;
import com.nhnacademy.exam.javamememberapi.member.dto.MemberUpdateRequest;
import com.nhnacademy.exam.javamememberapi.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
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

//    형님 생일축하합니다 10월에 생일이시라는데 오늘 생일하세요 그냥
//    축하드립니다. 만수무강하세요
//    주식 성투하시고 원하는곳 취업하세요.
//
//    고맙다 건승해라 장원아

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
