package org.example.v1.post.usedTrade.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.v1.member.domain.Member;

@Entity
@Getter
@NoArgsConstructor
public class UsedTradeStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private UsedTrade usedTrade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id")
    private Member buyer;

    @Setter
    @Enumerated(EnumType.STRING)
    private TradeStatusType status;


    public UsedTradeStatus(UsedTrade usedTrade, TradeStatusType status) {
        this.usedTrade = usedTrade;
        this.status = status;
    }

    public void updateBuyer(Member buyer) {
        this.buyer = buyer;
    }
}
