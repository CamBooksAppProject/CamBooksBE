package org.example.v1.post.usedTrade.service;

import org.example.v1.member.domain.Member;
import org.example.v1.member.repository.MemberRepository;
import org.example.v1.post.image.domain.PostImage;
import org.example.v1.post.image.repository.PostImageRepository;
import org.example.v1.post.usedTrade.domain.TradeStatusType;
import org.example.v1.post.usedTrade.domain.UsedTrade;
import org.example.v1.post.usedTrade.domain.UsedTradeStatus;
import org.example.v1.post.usedTrade.dto.UsedTradePreviewDto;
import org.example.v1.post.usedTrade.repository.UsedTradeRepository;
import org.example.v1.post.usedTrade.repository.UsedTradeStatusRepository;
import org.example.v1.postLike.repository.PostLikeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsedTradeStatusService {
    private final UsedTradeStatusRepository usedTradeStatusRepository;
    private final UsedTradeRepository usedTradeRepository;
    private final MemberRepository memberRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostImageRepository postImageRepository;

    public UsedTradeStatusService(UsedTradeStatusRepository usedTradeStatusRepository, UsedTradeRepository usedTradeRepository, MemberRepository memberRepository, PostLikeRepository postLikeRepository, PostImageRepository postImageRepository) {
        this.usedTradeStatusRepository = usedTradeStatusRepository;
        this.usedTradeRepository = usedTradeRepository;
        this.memberRepository = memberRepository;
        this.postLikeRepository = postLikeRepository;
        this.postImageRepository = postImageRepository;
    }

    public void updateStatus(Long postId, String email, TradeStatusType tradeStatusType) {
        UsedTrade post = usedTradeRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("글이 존재하지 않음"));
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않음"));

        UsedTradeStatus usedTradeStatus = usedTradeStatusRepository.findByUsedTrade(post);

        if (usedTradeStatus == null) {
            usedTradeStatus = new UsedTradeStatus(post, tradeStatusType);
        }

        usedTradeStatus.setBuyer(member);
        usedTradeStatus.setStatus(tradeStatusType);
    }
    public List<UsedTradePreviewDto> findTradePreviewsByBuyerEmail(String email) {
        Member buyer = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));

        List<UsedTradeStatus> statuses = usedTradeStatusRepository.findByBuyer(buyer);

        return statuses.stream()
                .map(status -> {
                    UsedTrade trade = status.getUsedTrade();
                    return new UsedTradePreviewDto(
                            trade.getId(),
                            trade.getTitle(),
                            trade.getPrice(),
                            trade.getViewCount(),
                            trade.getWriter().getUniversity().getNameKo(),
                            postLikeRepository.countByPost(trade),
                            postImageRepository.findByUsedTrade(trade).stream()
                                    .findFirst()
                                    .map(PostImage::getImageUrl)
                                    .orElse(null)
                    );
                })
                .toList();
    }

}
