package org.example.v1.postLike.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.v1.postLike.domain.PostType;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeRequestDto {
    Long postId;
    PostType postType;
}
