package org.example.v1.postLike.service;

import org.example.v1.comment.service.CommentService;
import org.example.v1.member.domain.Member;
import org.example.v1.member.repository.MemberRepository;
import org.example.v1.post.community.domain.Community;
import org.example.v1.post.community.dto.CommunityPreviewDto;
import org.example.v1.post.community.repository.CommunityRepository;
import org.example.v1.post.domain.Post;
import org.example.v1.post.generalForum.domain.GeneralForum;
import org.example.v1.post.generalForum.dto.GeneralForumPreviewDto;
import org.example.v1.post.generalForum.repository.GeneralForumRepository;
import org.example.v1.post.image.domain.CommunityImage;
import org.example.v1.post.image.domain.PostImage;
import org.example.v1.post.image.repository.CommunityImageRepository;
import org.example.v1.post.image.repository.PostImageRepository;
import org.example.v1.post.usedTrade.domain.UsedTrade;
import org.example.v1.post.usedTrade.dto.UsedTradePreviewDto;
import org.example.v1.post.usedTrade.repository.UsedTradeRepository;
import org.example.v1.postLike.domain.PostLike;
import org.example.v1.postLike.domain.PostType;
import org.example.v1.postLike.repository.PostLikeRepository;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final MemberRepository memberRepository;
    private final UsedTradeRepository usedTradeRepository;
    private final GeneralForumRepository generalForumRepository;
    private final CommunityRepository communityRepository;
    private final PostImageRepository postImageRepository;
    private final CommunityImageRepository communityImageRepository;
    private final CommentService commentService;

    public PostLikeService(PostLikeRepository postLikeRepository, MemberRepository memberRepository, UsedTradeRepository usedTradeRepository, GeneralForumRepository generalForumRepository, CommunityRepository communityRepository, PostImageRepository postImageRepository, CommunityImageRepository communityImageRepository, CommentService commentService) {
        this.postLikeRepository = postLikeRepository;
        this.memberRepository = memberRepository;
        this.usedTradeRepository = usedTradeRepository;
        this.generalForumRepository = generalForumRepository;
        this.communityRepository = communityRepository;
        this.postImageRepository = postImageRepository;
        this.communityImageRepository = communityImageRepository;
        this.commentService = commentService;
    }

    @Transactional
    public boolean likePost(String email, PostType postType, Long postId) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));

        Post post = getPostByTypeAndId(postType, postId);

        return postLikeRepository.findByMemberAndPost(member, post)
                .map(like -> {
                    postLikeRepository.delete(like);
                    return false; // 좋아요 취소됨
                })
                .orElseGet(() -> {
                    postLikeRepository.save(new PostLike(member, post));
                    return true; // 좋아요 성공
                });
    }

    @Transactional(readOnly = true)
    public long countLikes(PostType postType, Long postId) {
        Post post = getPostByTypeAndId(postType, postId);
        return postLikeRepository.countByPost(post);
    }

    private Post getPostByTypeAndId(PostType postType, Long postId) {
        return switch (postType) {
            case USED_TRADE -> usedTradeRepository.findById(postId)
                    .orElseThrow(() -> new IllegalArgumentException("중고거래 글 없음"));
            case GENERAL_FORUM -> generalForumRepository.findById(postId)
                    .orElseThrow(() -> new IllegalArgumentException("자유게시판 글 없음"));
            case COMMUNITY -> communityRepository.findById(postId)
                    .orElseThrow(() -> new IllegalArgumentException("커뮤니티 글 없음"));
            default -> throw new IllegalArgumentException("postType 오류");
        };
    }

    public Map<PostType, List<?>> getLikedPostPreviews(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));

        List<PostLike> likedPosts = postLikeRepository.findAllByMember(member);
        System.out.println(likedPosts);
        Map<PostType, List<?>> result = new HashMap<>();

        List<UsedTradePreviewDto> usedTrades = new ArrayList<>();
        List<CommunityPreviewDto> communities = new ArrayList<>();
        List<GeneralForumPreviewDto> forums = new ArrayList<>();

        for (PostLike postLike : likedPosts) {
            Post post = postLike.getPost();
            PostType type = post.getPostType();

            if (post.getPostType().equals(PostType.USED_TRADE)) {
                UsedTrade usedTrade = UsedTrade.class.cast(Hibernate.unproxy(post));
                usedTrades.add(toUsedTradePreviewDto(usedTrade));

            } else if (post.getPostType().equals(PostType.GENERAL_FORUM)) {
                GeneralForum forum = GeneralForum.class.cast(Hibernate.unproxy(post));
                forums.add(toGeneralForumPreviewDto(forum));

            } else if (post.getPostType().equals(PostType.COMMUNITY)) {
                Community community = Community.class.cast(Hibernate.unproxy(post));
                communities.add(toCommunityPreviewDto(community));
            }
        }

        if (!usedTrades.isEmpty()) result.put(PostType.USED_TRADE, usedTrades);
        if (!communities.isEmpty()) result.put(PostType.COMMUNITY, communities);
        if (!forums.isEmpty()) result.put(PostType.GENERAL_FORUM, forums);

        return result;
    }
    private UsedTradePreviewDto toUsedTradePreviewDto(UsedTrade usedTrade) {
        return new UsedTradePreviewDto(
                usedTrade.getId(),
                usedTrade.getTitle(),
                usedTrade.getPrice(),
                usedTrade.getViewCount(),
                usedTrade.getWriter().getUniversity().getNameKo(),
                postLikeRepository.countByPost(usedTrade),
                postImageRepository.findByUsedTrade(usedTrade).stream()
                        .findFirst()
                        .map(PostImage::getImageUrl)
                        .orElse(null)
        );
    }
    private CommunityPreviewDto toCommunityPreviewDto(Community community) {
        return new CommunityPreviewDto(
                community.getId(),
                community.getTitle(),
                community.getRecruitment(),
                community.getCurrentParticipants(),
                community.getRegion(),
                community.getCreatedAt(),
                communityImageRepository.findByCommunity(community).stream()
                        .findFirst()
                        .map(CommunityImage::getImageUrl)
                        .orElse(null),
                commentService.countComment(community.getId())
        );
    }
    private GeneralForumPreviewDto toGeneralForumPreviewDto(GeneralForum forum) {
        return new GeneralForumPreviewDto(
                forum.getId(),
                forum.getTitle(),
                forum.getContent(),
                forum.getWriter().getName(),
                forum.getCreatedAt(),
                postLikeRepository.countByPost(forum),
                commentService.countComment(forum.getId())
        );
    }
}
