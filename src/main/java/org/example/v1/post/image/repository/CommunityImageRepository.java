package org.example.v1.post.image.repository;

import org.example.v1.post.community.domain.Community;
import org.example.v1.post.image.domain.CommunityImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CommunityImageRepository extends JpaRepository<CommunityImage, Long> {
    List<CommunityImage> findByCommunity(Community community);
}
