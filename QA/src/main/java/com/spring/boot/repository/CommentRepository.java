package com.spring.boot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.boot.domain.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
