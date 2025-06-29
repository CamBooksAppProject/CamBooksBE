package org.example.v1.post.community.service;

import org.example.v1.comment.repository.CommunityCommentRepository;
import org.example.v1.comment.service.CommunityCommentService;
import org.example.v1.member.domain.Member;
import org.example.v1.member.repository.MemberRepository;
import org.example.v1.post.community.domain.Community;
import org.example.v1.post.community.dto.CommunityPreviewDto;
import org.example.v1.post.community.dto.CommunityRequestDto;
import org.example.v1.post.community.dto.CommunityResponseDto;
import org.example.v1.post.community.repository.CommunityRepository;
import org.example.v1.post.image.domain.CommunityImage;
import org.example.v1.post.image.repository.CommunityImageRepository;
import org.example.v1.searchResult.dto.SearchResultDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CommunityService {
    private final CommunityRepository communityRepository;
    private final MemberRepository memberRepository;
    private final CommunityImageRepository communityImageRepository;
    private final CommunityCommentService communityCommentService;

    public CommunityService(CommunityRepository communityRepository, MemberRepository memberRepository, CommunityImageRepository communityImageRepository, CommunityCommentService communityCommentService) {
        this.communityRepository = communityRepository;
        this.memberRepository = memberRepository;
        this.communityImageRepository = communityImageRepository;
        this.communityCommentService = communityCommentService;
    }
    public CommunityResponseDto create(String email, CommunityRequestDto dto, List<MultipartFile> images) {
        Member writer = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));

        Community post = new Community(
                dto.getTitle(),
                writer,
                LocalDateTime.now(),
                dto.getRegion(),
                dto.getRecruitment(),
                dto.getIntroduction(),
                dto.getMaxParticipants(),
                dto.getStartDateTime(),
                dto.getEndDateTime()
        );

        Community saved = communityRepository.save(post);
        if (images != null) {
            for (MultipartFile image : images) {
                String filename = UUID.randomUUID() + "_" + image.getOriginalFilename();
                Path savePath = Paths.get("upload/community").resolve(filename);

                try {
                    Files.createDirectories(savePath.getParent());
                    image.transferTo(savePath);
                    communityImageRepository.save(new CommunityImage(saved, "/upload/community/" + filename));
                } catch (IOException e) {
                    throw new RuntimeException("이미지 저장 실패", e);
                }
            }
        }

        return new CommunityResponseDto(
                saved.getId(),
                saved.getTitle(),
                saved.getRegion(),
                saved.getRecruitment(),
                saved.getIntroduction(),
                saved.getCurrentParticipants(),
                saved.getMaxParticipants(),
                saved.getCreatedAt(),
                saved.getStartDateTime(),
                saved.getEndDateTime()
        );
    }

    public List<Community> search(String keyword) {
        return communityRepository.findByTitleContaining(keyword);
    }

    public CommunityResponseDto getById(Long postId) {
        Community post = communityRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("글이 존재하지 않음"));
        List<CommunityImage> images = communityImageRepository.findByCommunity(post);
        List<String> imageUrls = images.stream()
                .map(CommunityImage::getImageUrl)
                .toList();
        return new CommunityResponseDto(
                post.getId(),
                post.getTitle(),
                post.getRegion(),
                post.getRecruitment(),
                post.getIntroduction(),
                post.getCurrentParticipants(),
                post.getMaxParticipants(),
                post.getCreatedAt(),
                post.getStartDateTime(),
                post.getEndDateTime(),
                imageUrls,
                communityCommentService.getCommentList(post.getId()),
                communityCommentService.countComment(post.getId())
        );
    }
    public List<CommunityPreviewDto> findAll() {
        List<Community> communities = communityRepository.findAll();
        return getCommunityPreviewDtoList(communities);
    }

    private List<CommunityPreviewDto> getCommunityPreviewDtoList(List<Community> communityList){
    return communityList.stream()
            .map(post->{
                String thumbnail = communityImageRepository.findByCommunity(post).stream()
                        .findFirst()
                        .map(CommunityImage::getImageUrl)
                        .orElse(null);
                return new CommunityPreviewDto(
                        post.getId(),
                        post.getTitle(),
                        post.getRecruitment(),
                        post.getCurrentParticipants(),
                        post.getRegion(),
                        post.getCreatedAt(),
                        thumbnail,
                        communityCommentService.countComment(post.getId())
                );
            }).toList();
    }
    public boolean checkWriterIdIsMyId(String email, Long postId){
        Member m1 = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수가 없습니다."));
        Member m2 = communityRepository.findById(postId).get().getWriter();
        return m1.getId().equals(m2.getId());
    }

    public List<SearchResultDto> searchByKeyword(String keyword) {
        return communityRepository
                .findByTitleContaining(keyword)
                .stream()
                .map(post -> new SearchResultDto(
                        "community",
                        post.getId(),
                        post.getTitle(),
                        null,
                        post.getWriter().getName(),
                        post.getCreatedAt()
                ))
                .toList();
    }
}

