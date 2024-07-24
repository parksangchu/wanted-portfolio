package com.wanted.portfolio.like.repository;

import com.wanted.portfolio.like.model.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
}
