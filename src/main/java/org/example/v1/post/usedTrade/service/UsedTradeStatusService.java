package org.example.v1.post.usedTrade.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.v1.member.domain.Member;
import org.example.v1.member.repository.MemberRepository;
import org.example.v1.notification.domain.Notification;
import org.example.v1.notification.domain.NotificationType;
import org.example.v1.notification.repository.NotificationRepository;
import org.example.v1.notification.repository.NotificationTypeRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsedTradeStatusService {
    private final UsedTradeStatusRepository usedTradeStatusRepository;
    private final UsedTradeRepository usedTradeRepository;
    private final MemberRepository memberRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostImageRepository postImageRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationTypeRepository notificationTypeRepository;

    public UsedTradeStatusService(UsedTradeStatusRepository usedTradeStatusRepository, UsedTradeRepository usedTradeRepository, MemberRepository memberRepository, PostLikeRepository postLikeRepository, PostImageRepository postImageRepository, NotificationRepository notificationRepository, NotificationTypeRepository notificationTypeRepository) {
        this.usedTradeStatusRepository = usedTradeStatusRepository;
        this.usedTradeRepository = usedTradeRepository;
        this.memberRepository = memberRepository;
        this.postLikeRepository = postLikeRepository;
        this.postImageRepository = postImageRepository;
        this.notificationRepository = notificationRepository;
        this.notificationTypeRepository = notificationTypeRepository;
    }

    @Transactional
    public void updateStatus(Long postId, String email, Long buyerId) {
        UsedTrade post = usedTradeRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("글이 존재하지 않음"));
        Member seller = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않음 =. email : " + email));
        Member buyer = memberRepository.findById(buyerId)
                .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않음 => id : " + buyerId));
        UsedTradeStatus status = usedTradeStatusRepository.findByUsedTrade(post);

        status.updateBuyer(buyer);
        status.setStatus(TradeStatusType.COMPLETED);

        usedTradeStatusRepository.save(status);

        NotificationType byId = notificationTypeRepository.findById(4L)
                .orElseThrow(() -> new EntityNotFoundException("해당 타입의 NotificationType이 없습니다."));

        Notification notificationForBuyer = Notification.builder()
                .notificationType(byId)
                .content("["+ post.getTitle()+"]" + " 구매가 완료되었습니다.")
                .navigateId(post.getId())
                .member(buyer)
                .build();
        Notification notificationForSeller = Notification.builder()
                .notificationType(byId)
                .content("["+ post.getTitle()+"]" + " 판매가 완료되었습니다.")
                .navigateId(post.getId())
                .member(seller)
                .build();
        notificationRepository.save(notificationForBuyer);
        notificationRepository.save(notificationForSeller);

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
                                    .orElse(null),
                            status.getStatus()
                    );
                })
                .toList();
    }

}
