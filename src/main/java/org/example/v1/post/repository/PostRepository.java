package org.example.v1.post.repository;

import org.example.v1.member.domain.Member;
import org.example.v1.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface PostRepository<T extends Post> extends JpaRepository<T, Long> {
    List<T> findByWriter(Member writer);
}
