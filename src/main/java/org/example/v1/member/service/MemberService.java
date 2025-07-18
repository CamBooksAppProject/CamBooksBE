package org.example.v1.member.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.example.v1.comment.repository.CommentRepository;
import org.example.v1.mailauth.MailService;
import org.example.v1.member.domain.Member;
import org.example.v1.member.dto.*;
import org.example.v1.member.repository.MemberRepository;
import org.example.v1.post.community.repository.CommunityJoinRepository;
import org.example.v1.post.community.repository.CommunityRepository;
import org.example.v1.post.generalForum.repository.GeneralForumRepository;
import org.example.v1.post.usedTrade.repository.UsedTradeRepository;
import org.example.v1.postLike.repository.PostLikeRepository;
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
    private final GeneralForumRepository generalForumRepository;
    private final CommunityRepository communityRepository;
    private final UsedTradeRepository usedTradeRepository;
    private final CommunityJoinRepository communityJoinRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;

    public MemberService(MemberRepository memberRepository, UniversityRepository universityRepository, MailService mailService, PasswordEncoder passwordEncoder, GeneralForumRepository generalForumRepository, CommunityRepository communityRepository, UsedTradeRepository usedTradeRepository, CommunityJoinRepository communityJoinRepository, CommentRepository commentRepository, PostLikeRepository postLikeRepository) {
        this.memberRepository = memberRepository;
        this.universityRepository = universityRepository;
        this.mailService = mailService;
        this.passwordEncoder = passwordEncoder;
        this.generalForumRepository = generalForumRepository;
        this.communityRepository = communityRepository;
        this.usedTradeRepository = usedTradeRepository;
        this.communityJoinRepository = communityJoinRepository;
        this.commentRepository = commentRepository;
        this.postLikeRepository = postLikeRepository;
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
                .nickname(dto.getNickname())
                .memberId(dto.getMemberId())
                .password(passwordEncoder.encode(dto.getPassword()))
                .address(dto.getAddress())
                .university(universityRepository.findById(dto.getUniversityId()).orElseThrow(
                        () -> new IllegalArgumentException("해당 대학이 존재하지 않습니다.")
                ))
                .build();

        return memberRepository.save(newMember);
    }

    public boolean checkId(String id) {
        if (memberRepository.findByMemberId(id).isPresent()) {
            return false;
        }
        return true;
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

    public String findMemberAddress(String email){
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 이메일입니다."));
        return member.getAddress();
    }
    public String updateMemberAddress(String email, String address){
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 이메일입니다."));
        member.updateAddress(address);
        return member.getAddress();
    }

    public MemberResponseDto getMember(String email){
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 이메일입니다."));
        MemberResponseDto memberResponseDto = new MemberResponseDto();
        memberResponseDto.setName(member.getName());
        memberResponseDto.setEmail(member.getEmail());
        memberResponseDto.setAddress(member.getAddress());
        memberResponseDto.setUniversity(member.getUniversity().getNameKo());
        return memberResponseDto;
    }

    public void checkPassword(String email, String password){
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("사용자 없음"));
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }
    }

    public void updatePassword(String email, String newPassword) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("사용자 없음"));
        if (newPassword.length() < 8) {
            throw new IllegalArgumentException("새 비밀번호는 최소 8자 이상이어야 합니다.");
        }
        member.updatePassword(passwordEncoder.encode(newPassword));
    }

    public void createNickname(String email, String nickname) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("사용자 없음"));
        member.setNickname(nickname);
    }

    public String getNickname(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("사용자 없음"));
        return member.getNickname();
    }

    public String updateNickname(String email, String nickname) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("사용자 없음"));
        member.setNickname(nickname);
        return member.getNickname();
    }

    public void checkUnivAndEMail(IdFindRequestDto dto) {
        String university = dto.getUniversity();
        String email = dto.getEmail();
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("이메일이 존재하지 않습니다."));
        if(university.isEmpty() || !university.equals(member.getUniversity().getNameKo())) {
            throw new IllegalStateException("학교 이름을 다시 입력해주십시오.");
        }
    }
    public String validateAuthCodeAndGetUserId(String email, String code) {
        boolean verified = mailService.verifyCode(email, code);
        if(!verified) {
            throw new IllegalStateException("인증 다시 시도하세요");
        }else{
            Member member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException("시용자 없음"));
            return member.getMemberId();
        }
    }
    public void checkMemberIdAndEmail(PasswordFindRequestDto dto) {
        String memberId = dto.getMemberId();
        String email = dto.getEmail();
        Member m1 = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당 아이디가 존재하지 않음"));
        Member m2 = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일 존재하지 않음"));
        if(!m1.getId().equals(m2.getId())) {
            throw new IllegalStateException("아이디와 이메일의 소유주가 다름");
        }
    }
    public boolean validateAuthCode(String email, String code) {
        boolean verified = mailService.verifyCode(email, code);
        if(!verified) {
            return false;
        }else{
            Member member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException("사용자 없음"));
            return true;
        }
    }
    public void deleteMember(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("사용자 없음"));
        postLikeRepository.deleteAllByMember(member);
        commentRepository.deleteAllByWriter(member);
        generalForumRepository.deleteAllByWriter(member);
        communityJoinRepository.deleteAllByParticipant(member);
        communityRepository.deleteAllByWriter(member);
        usedTradeRepository.deleteAllByWriter(member);
        memberRepository.delete(member);
    }
}
