package org.example.v1.post.usedTrade.repository;

import org.example.v1.member.domain.Member;
import org.example.v1.post.repository.PostRepository;
import org.example.v1.post.usedTrade.domain.UsedTrade;

import java.util.List;

public interface UsedTradeRepository extends PostRepository<UsedTrade> {
    List<UsedTrade> findByTitleContaining(String keyword);
    void deleteAllByWriter(Member writer);
}
