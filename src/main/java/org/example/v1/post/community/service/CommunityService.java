package org.example.v1.post.community.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.example.v1.chat.domain.ChatParticipant;
import org.example.v1.chat.domain.ChatRoom;
import org.example.v1.chat.domain.GroupChatRoomOwner;
import org.example.v1.chat.repository.ChatParticipantRepository;
import org.example.v1.chat.repository.ChatRoomRepository;
import org.example.v1.chat.repository.GroupChatRoomOwnerRepository;
import org.example.v1.chat.service.ChatService;
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
import org.example.v1.postLike.repository.PostLikeRepository;
import org.example.v1.searchResult.dto.SearchResultDto;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CommunityService {
    private final CommunityRepository communityRepository;
    private final MemberRepository memberRepository;
    private final CommunityImageRepository communityImageRepository;
    private final CommunityCommentRepository communityCommentRepository;
    private final CommunityCommentService communityCommentService;
    private final PostLikeRepository postLikeRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final GroupChatRoomOwnerRepository groupChatRoomOwnerRepository;
    private final ChatService chatService;

    public CommunityService(CommunityRepository communityRepository, MemberRepository memberRepository, CommunityImageRepository communityImageRepository, CommunityCommentRepository communityCommentRepository, CommunityCommentService communityCommentService, PostLikeRepository postLikeRepository, ChatService chatService, ChatRoomRepository chatRoomRepository, ChatParticipantRepository chatParticipantRepository, GroupChatRoomOwnerRepository groupChatRoomOwnerRepository, ChatService chatService1) {
        this.communityRepository = communityRepository;
        this.memberRepository = memberRepository;
        this.communityImageRepository = communityImageRepository;
        this.communityCommentRepository = communityCommentRepository;
        this.communityCommentService = communityCommentService;
        this.postLikeRepository = postLikeRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.chatParticipantRepository = chatParticipantRepository;
        this.groupChatRoomOwnerRepository = groupChatRoomOwnerRepository;
        this.chatService = chatService1;
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
//        채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .name(dto.getTitle())
                .isGroupChat("Y")
                .build();
        chatRoomRepository.save(chatRoom);
//        채팅 참여자로 개설자를 추가
        ChatParticipant chatParticipant = ChatParticipant.builder()
                .chatRoom(chatRoom)
                .member(writer)
                .build();
        GroupChatRoomOwner owner = GroupChatRoomOwner.builder()
                .chatRoom(chatRoom)
                .community(post)
                .owner(writer)
                .build();
        chatParticipantRepository.save(chatParticipant);
        groupChatRoomOwnerRepository.save(owner);


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
                saved.getEndDateTime(),
                saved.getWriter().getId(),
                chatRoom.getId()
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
        GroupChatRoomOwner owner = groupChatRoomOwnerRepository.findByCommunity(post)
                .orElseThrow(() -> new IllegalArgumentException("<UNK> <UNK> <UNK>"));
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
                communityCommentService.countComment(post.getId()),
                post.getWriter().getId(),
                1L
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

    @Transactional
    public void deleteCommunity(String email, Long postId) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자 찾을 수 없음"));
        Community community = communityRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 커뮤니티 글 없음"));
        Optional<GroupChatRoomOwner> byOwner = groupChatRoomOwnerRepository.findByCommunity(community);
        ChatRoom chatRoom = byOwner.map(GroupChatRoomOwner::getChatRoom).orElse(null);
        if(community.getWriter().getId().equals(member.getId())){
            if(chatRoom != null) {
                chatService.leaveCommunityChatRoom(chatRoom.getId(), email);
            }
            communityCommentRepository.deleteAllByCommunity(community);
            postLikeRepository.deleteAllByPost(community);
            communityRepository.delete(community);
        }else{
            throw new IllegalArgumentException("삭제가 불가능합니다.");
        }
    }

    public void updateCommunity(String email, Long postId, CommunityRequestDto communityRequestDto) {
        Member member  = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자 찾을 수 없음"));
        Community community = communityRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 커뮤니티 글 없음"));
        if(!community.getWriter().getId().equals(member.getId())){
            throw new IllegalArgumentException("커뮤니티글 작성자가 아닙니다.");
        }
        community.updateField(
                communityRequestDto.getTitle(),
                communityRequestDto.getRegion(),
                communityRequestDto.getRecruitment(),
                communityRequestDto.getIntroduction(),
                communityRequestDto.getMaxParticipants(),
                communityRequestDto.getStartDateTime(),
                communityRequestDto.getEndDateTime()
        );
        communityRepository.save(community);
    }
}

