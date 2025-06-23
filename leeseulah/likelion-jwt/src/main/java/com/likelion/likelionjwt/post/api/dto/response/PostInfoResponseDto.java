package com.likelion.likelionjwt.post.api.dto.response;

import com.likelion.likelionjwt.post.domain.Post;
import lombok.Builder;

@Builder
public record PostInfoResponseDto(
        String title,
        String content,
        String writer
) {
    public static PostInfoResponseDto from(Post post) {
        return PostInfoResponseDto.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .writer(post.getMember().getName())
                .build();
    }
}
