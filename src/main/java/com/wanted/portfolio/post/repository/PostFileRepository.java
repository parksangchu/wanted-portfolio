package com.wanted.portfolio.post.repository;

import com.wanted.portfolio.post.model.PostFile;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PostFileRepository extends JpaRepository<PostFile, Long> {

    @Modifying
    @Query("DELETE FROM PostFile pf WHERE pf.post.id = :postId")
    void deleteAllByPostId(Long postId);

    List<PostFile> findAllByPostId(Long postId);
}
