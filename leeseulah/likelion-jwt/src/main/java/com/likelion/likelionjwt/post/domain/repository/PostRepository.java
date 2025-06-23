package com.likelion.likelionjwt.post.domain.repository;


import com.likelion.likelionjwt.member.domain.Member;
import com.likelion.likelionjwt.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByMember(Member member);
}
