package com.wanted.portfolio.comment.repository;

import com.wanted.portfolio.comment.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    Page<Comment> findAllByPostIdAndDeletedAtIsNull(Long postId, Pageable pageable);
}
