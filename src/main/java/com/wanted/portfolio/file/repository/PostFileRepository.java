package com.wanted.portfolio.file.repository;

import com.wanted.portfolio.file.model.PostFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostFileRepository extends JpaRepository<PostFile, Long> {
}
