package org.example.v1.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.v1.university.domain.University;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponseDto {
    private String name;
    private String email;
    private String university;
    private Long univId;
    private String address;
    private String profileImage;
}
