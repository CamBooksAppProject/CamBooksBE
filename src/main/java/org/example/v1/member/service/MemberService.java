package org.example.v1.member.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.example.v1.mailauth.MailService;
import org.example.v1.member.domain.Member;
import org.example.v1.member.dto.MemberListResDto;
import org.example.v1.member.dto.MemberLoginRequestDto;
import org.example.v1.member.dto.MemberSaveReqDto;
import org.example.v1.member.repository.MemberRepository;
import org.example.v1.university.repository.UniversityRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final UniversityRepository universityRepository;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, UniversityRepository universityRepository, MailService mailService, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.universityRepository = universityRepository;
        this.mailService = mailService;
        this.passwordEncoder = passwordEncoder;
    }

    public Member create(MemberSaveReqDto dto) {
        if (memberRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalStateException("이미 존재하는 이메일입니다.");
        }

        // 🔐 이메일 인증 여부 확인
        if (!mailService.isVerified(dto.getEmail())) {
            throw new IllegalStateException("이메일 인증이 완료되지 않았습니다.");
        }

        Member newMember = Member.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .memberId(dto.getMemberId())
                .password(passwordEncoder.encode(dto.getPassword()))
                .university(universityRepository.findById(dto.getUniversityId()).orElseThrow(
                        () -> new IllegalArgumentException("해당 대학이 존재하지 않습니다.")
                ))
                .build();

        return memberRepository.save(newMember);
    }
    public Member login(MemberLoginRequestDto memberLoginRequestDto) {
        Member member = memberRepository.findByMemberId(memberLoginRequestDto.getMemberId()).orElseThrow(()->new EntityNotFoundException("존재하지 않는 ID입니다."));
        if(!passwordEncoder.matches(memberLoginRequestDto.getPassword(), member.getPassword())) {
            throw new IllegalStateException("비밀번호가 일치하지 않습니다.");
        }
        return member;
    }

    public List<MemberListResDto> findAll(){
        List<Member> members = memberRepository.findAll();
        List<MemberListResDto> memberListResDtos = new ArrayList<>();
        for(Member member : members){
            MemberListResDto memberListResDto = new MemberListResDto();
            memberListResDto.setId(member.getId());
            memberListResDto.setName(member.getName());
            memberListResDto.setEmail(member.getEmail());
            memberListResDtos.add(memberListResDto);
        }
        return memberListResDtos;
    }
}
