package com.wanted.portfolio.file.repository;

import com.wanted.portfolio.file.model.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {
}
