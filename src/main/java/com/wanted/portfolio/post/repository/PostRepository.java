package com.wanted.portfolio.post.repository;

import com.wanted.portfolio.post.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
