package org.example.v1.searchResult.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultDto {
    private String postType;      // 게시판 종류: generalForum, usedTrade, community
    private Long id;              // 게시글 ID
    private String title;         // 제목
    private String content;       // 내용 (community는 null 가능)
    private String writerName;    // 작성자명
    private LocalDateTime createdAt;  // 생성일
}

