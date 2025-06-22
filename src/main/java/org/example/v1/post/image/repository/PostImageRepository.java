package org.example.v1.post.image.repository;

import org.example.v1.post.image.domain.PostImage;
import org.example.v1.post.usedTrade.domain.UsedTrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    List<PostImage> findByUsedTrade(UsedTrade usedTrade);
}
