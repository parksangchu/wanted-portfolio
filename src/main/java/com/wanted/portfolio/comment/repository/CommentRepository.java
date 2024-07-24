package com.wanted.portfolio.comment.repository;

import com.wanted.portfolio.comment.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
}
