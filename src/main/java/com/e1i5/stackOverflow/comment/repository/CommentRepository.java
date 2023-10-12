package com.e1i5.stackOverflow.comment.repository;

import com.e1i5.stackOverflow.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findAllByQuestionQuestionId(Long questionId, Pageable pageable);

}
