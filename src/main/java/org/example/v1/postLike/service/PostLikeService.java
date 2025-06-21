package org.example.v1.postLike.service;

import org.example.v1.member.domain.Member;
import org.example.v1.member.repository.MemberRepository;
import org.example.v1.post.community.repository.CommunityRepository;
import org.example.v1.post.domain.Post;
import org.example.v1.post.generalForum.repository.GeneralForumRepository;
import org.example.v1.post.usedTrade.repository.UsedTradeRepository;
import org.example.v1.postLike.domain.PostLike;
import org.example.v1.postLike.repository.PostLikeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final MemberRepository memberRepository;
    private final UsedTradeRepository usedTradeRepository;
    private final GeneralForumRepository generalForumRepository;
    private final CommunityRepository communityRepository;

    public PostLikeService(PostLikeRepository postLikeRepository, MemberRepository memberRepository, UsedTradeRepository usedTradeRepository, GeneralForumRepository generalForumRepository, CommunityRepository communityRepository) {
        this.postLikeRepository = postLikeRepository;
        this.memberRepository = memberRepository;
        this.usedTradeRepository = usedTradeRepository;
        this.generalForumRepository = generalForumRepository;
        this.communityRepository = communityRepository;
    }

    @Transactional
    public void likePost(String postType, Long postId, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));

        Post post = getPostByTypeAndId(postType, postId);

        postLikeRepository.findByMemberAndPost(member, post)
                .ifPresentOrElse(
                        postLikeRepository::delete,
                        () -> postLikeRepository.save(new PostLike(member, post))
                );
    }

    @Transactional(readOnly = true)
    public long countLikes(String postType, Long postId) {
        Post post = getPostByTypeAndId(postType, postId);
        return postLikeRepository.countByPost(post);
    }

    private Post getPostByTypeAndId(String postType, Long postId) {
        return switch (postType) {
            case "UserTrade" -> usedTradeRepository.findById(postId)
                    .orElseThrow(() -> new IllegalArgumentException("중고거래 글 없음"));
            case "FreeBoard" -> generalForumRepository.findById(postId)
                    .orElseThrow(() -> new IllegalArgumentException("자유게시판 글 없음"));
            case "Community" -> communityRepository.findById(postId)
                    .orElseThrow(() -> new IllegalArgumentException("커뮤니티 글 없음"));
            default -> throw new IllegalArgumentException("<UNK> <UNK> <UNK>");
        };
    }
}
