package org.example.v1.member.controller;

import org.example.v1.common.auth.JwtTokenProvider;
import org.example.v1.mailauth.MailService;
import org.example.v1.member.dto.*;
import org.example.v1.member.domain.Member;
import org.example.v1.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cambooks/member")
public class MemberController {
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final MailService mailService;

    public MemberController(MemberService memberService, JwtTokenProvider jwtTokenProvider, MailService mailService) {
        this.memberService = memberService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.mailService = mailService;
    }
    @PostMapping("/create")
    public ResponseEntity<?> memberCreate(@RequestBody MemberSaveReqDto memberSaveReqDto) {
        Member member = memberService.create(memberSaveReqDto);
        return new ResponseEntity<>(member.getMemberId(), HttpStatus.CREATED);
    }
    @PostMapping("/check-id")
    public ResponseEntity<?> checkIdExist(@RequestBody CheckIdDto dto) {
        if (memberService.checkId(dto.getMemberId())) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
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
    @PostMapping("/nickname")
    public ResponseEntity<?> createNickname(@AuthenticationPrincipal UserDetails userDetails, String nickname) {
        String email = userDetails.getUsername();
        memberService.createNickname(email, nickname);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/address")
    public ResponseEntity<?> createAddress(@AuthenticationPrincipal UserDetails userDetails, @RequestBody AddressUpdateRequest request) {
        String email = userDetails.getUsername();
        memberService.updateMemberAddress(email, request.getAddress());
        return ResponseEntity.ok("address updated");
    }

    @PostMapping("/check-password")
    public ResponseEntity<?> checkPassword(@AuthenticationPrincipal UserDetails userDetails,
                                            @RequestBody CheckPasswordDto dto) {
        String email = userDetails.getUsername();
        memberService.checkPassword(email, dto.getPassword());
        return ResponseEntity.ok("password checked");
    }
    @PostMapping("/update-password")
    public ResponseEntity<?> updatePassword(@AuthenticationPrincipal UserDetails userDetails,@RequestBody CheckPasswordDto dto) {
        String email = userDetails.getUsername();
        memberService.updatePassword(email, dto.getPassword());
        return ResponseEntity.ok("password updated");
    }

    @PutMapping("/address")
    public ResponseEntity<?> updateAddress(@AuthenticationPrincipal UserDetails userDetails,
                                           @RequestBody AddressUpdateRequest request) {
        String email = userDetails.getUsername();
        memberService.updateMemberAddress(email, request.getAddress());
        return ResponseEntity.ok("address updated");
    }

    @GetMapping("/list")
    public ResponseEntity<?> memberList() {
        List<MemberListResDto> dtos = memberService.findAll();
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/address")
    public ResponseEntity<?> memberAddress(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        String memberAddress = memberService.findMemberAddress(email);
        return new ResponseEntity<>(memberAddress, HttpStatus.OK);
    }

    @GetMapping("/info")
    public ResponseEntity<?> memberInfo(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        memberService.getMember(email);
        return new ResponseEntity<>(memberService.getMember(email), HttpStatus.OK);
    }

    @GetMapping("/nickname")
    public ResponseEntity<?> memberNickname(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        return new ResponseEntity<>(memberService.getNickname(email), HttpStatus.OK);
    }

    @PutMapping("/nickname")
    public ResponseEntity<?> updateNickname(@AuthenticationPrincipal UserDetails userDetails, String nickname) {
        String email = userDetails.getUsername();
        memberService.updateNickname(email, nickname);
        return new ResponseEntity<>("nickname updated", HttpStatus.OK);
    }

    @PostMapping("/find-id/send")
    public ResponseEntity<String> sendEmailForIdRecovery(@RequestBody IdFindRequestDto dto) {
        memberService.checkUnivAndEMail(dto);
        mailService.sendAuthCode(dto.getEmail());
        return ResponseEntity.ok("인증 코드가 전송되었습니다.");
    }

    @PostMapping("/find-id/verified")
    public ResponseEntity<String> verifyCodeAndReturnId(@RequestParam String email, @RequestParam String code) {
        String id = memberService.validateAuthCodeAndGetUserId(email, code);
        return ResponseEntity.ok(id);
    }

    @PostMapping("/find-password/send")
    public ResponseEntity<String> sendEmailForPasswordRecovery(@RequestBody PasswordFindRequestDto dto) {
        memberService.checkMemberIdAndEmail(dto);
        mailService.sendAuthCode(dto.getEmail());
        return ResponseEntity.ok("인증 코드가 전송되었습니다.");
    }
    @PostMapping("/find-password/verified")
    public boolean verifyCodeAndReturnPassword(@RequestParam String email, @RequestParam String code) {
        return memberService.validateAuthCode(email, code);
    }
    @PostMapping("/find-password/new-password")
    public ResponseEntity<?> setNewPassword(@RequestBody ResetPasswordReqDto dto) {
        memberService.updatePassword(dto.getEmail(), dto.getNewPassword());
        return ResponseEntity.ok("password updated");
    }
}
