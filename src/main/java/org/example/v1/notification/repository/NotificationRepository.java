package org.example.v1.notification.repository;

import org.example.v1.member.domain.Member;
import org.example.v1.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByMember(Member member);
}
