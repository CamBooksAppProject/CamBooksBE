package org.example.v1.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberSaveReqDto {
    private String name;
    private String nickname;
    private String email;
    private String memberId;
    private String password;
    private Long universityId;
    private String address;
}
