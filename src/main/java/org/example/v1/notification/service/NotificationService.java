package org.example.v1.notification.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.v1.member.domain.Member;
import org.example.v1.member.repository.MemberRepository;
import org.example.v1.notification.domain.Notification;
import org.example.v1.notification.dto.NotificationResponseDto;
import org.example.v1.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;

    public NotificationService(NotificationRepository notificationRepository, MemberRepository memberRepository) {
        this.notificationRepository = notificationRepository;
        this.memberRepository = memberRepository;
    }

    public List<NotificationResponseDto> findAll(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(()-> new EntityNotFoundException("Member not found - " + email));
        List<Notification> notifications = notificationRepository.findAllByMember(member);
        if(!notifications.isEmpty()) {
            List<NotificationResponseDto> notificationResponseDtos = new ArrayList<>();
            for (Notification notification : notifications) {
                notificationResponseDtos.add(NotificationResponseDto.builder()
                        .id(notification.getId())
                        .noticeTypeId(notification.getNotificationType().getId())
                        .navigateId(notification.getNavigateId())
                        .content(notification.getContent())
                        .build());
            }
            return notificationResponseDtos;
        }
        return null;
    }

}
