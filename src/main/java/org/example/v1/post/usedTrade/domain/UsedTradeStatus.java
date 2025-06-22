package org.example.v1.post.usedTrade.domain;

import jakarta.persistence.*;
import org.example.v1.member.domain.Member;

@Entity
public class UsedTradeStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private UsedTrade usedTrade;

    @ManyToOne
    private Member buyer;

    @Enumerated(EnumType.STRING)
    private TradeStatusType status;
}
