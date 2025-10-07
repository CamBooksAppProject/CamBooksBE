package org.example.v1.member.domain;

import jakarta.persistence.*;
import lombok.*;
import org.example.v1.university.domain.University;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Column(nullable = false, unique = true)
    private String memberId;
    private String password;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.USER;

    @Column(nullable = false, unique = true)
    private String email;

    @Setter
    private String nickname;

    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    private University university;
    @Setter
    private String profileImage;

    public void updatePassword(String encodedPassword) {
        if (encodedPassword == null || encodedPassword.length() < 10) {
            throw new IllegalArgumentException("비밀번호 형식이 잘못되었습니다.");
        }
        this.password = encodedPassword;
    }
    public void updateAddress(String address) {
        if (address == null || address.length() < 5) {
            throw new IllegalArgumentException("주소는 5자 이상 입력해주세요.");
        }
        this.address = address;
    }

    public void setProfileImage(String urlPath) {
        this.profileImage = urlPath;
    }
}
