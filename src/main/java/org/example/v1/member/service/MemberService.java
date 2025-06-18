package org.example.v1.member.service;

import jakarta.transaction.Transactional;
import org.example.v1.mailauth.MailService;
import org.example.v1.member.domain.Member;
import org.example.v1.member.dto.MemberSaveReqDto;
import org.example.v1.member.repository.MemberRepository;
import org.example.v1.university.repository.UniversityRepository;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final UniversityRepository universityRepository;
    private final MailService mailService;

    public MemberService(MemberRepository memberRepository, UniversityRepository universityRepository, MailService mailService) {
        this.memberRepository = memberRepository;
        this.universityRepository = universityRepository;
        this.mailService = mailService;
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
                .password(dto.getPassword())  // 실무에선 반드시 암호화!
                .university(universityRepository.findById(dto.getUniversityId()).orElseThrow(
                        () -> new IllegalArgumentException("해당 대학이 존재하지 않습니다.")
                ))
                .build();

        return memberRepository.save(newMember);
    }
}
