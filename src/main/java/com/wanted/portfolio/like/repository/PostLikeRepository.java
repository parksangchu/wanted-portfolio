package com.wanted.portfolio.like.repository;

import com.wanted.portfolio.like.model.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
}
