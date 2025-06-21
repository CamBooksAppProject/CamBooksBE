package org.example.v1.member.controller;

import org.example.v1.common.auth.JwtTokenProvider;
import org.example.v1.member.domain.Member;
import org.example.v1.member.dto.MemberListResDto;
import org.example.v1.member.dto.MemberLoginRequestDto;
import org.example.v1.member.dto.MemberSaveReqDto;
import org.example.v1.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cambooks/member")
public class MemberController {
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    public MemberController(MemberService memberService, JwtTokenProvider jwtTokenProvider) {
        this.memberService = memberService;
        this.jwtTokenProvider = jwtTokenProvider;
    }
    @PostMapping("/create")
    public ResponseEntity<?> memberCreate(@RequestBody MemberSaveReqDto memberSaveReqDto) {
        Member member = memberService.create(memberSaveReqDto);
        return new ResponseEntity<>(member.getMemberId(), HttpStatus.CREATED);
    }

    @PostMapping("/doLogin")
    public ResponseEntity<?> doLogin(@RequestBody MemberLoginRequestDto memberLoginRequestDto) {

        Member member = memberService.login(memberLoginRequestDto);

        String jwtToken = jwtTokenProvider.createtoken(member.getEmail(), member.getUniversity().toString());
        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("id", member.getId());
        loginInfo.put("token", jwtToken);
        return new ResponseEntity<>(loginInfo, HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<?> memberList() {
        List<MemberListResDto> dtos = memberService.findAll();
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }
}
