package org.example.v1.post.usedTrade.repository;

import org.example.v1.member.domain.Member;
import org.example.v1.post.usedTrade.domain.UsedTrade;
import org.example.v1.post.usedTrade.domain.UsedTradeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsedTradeStatusRepository extends JpaRepository<UsedTradeStatus, Long> {
    List<UsedTradeStatus> findByBuyer(Member buyer);
    UsedTradeStatus findByUsedTrade(UsedTrade usedTrade);
}
