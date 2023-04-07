package com.spring.boot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.boot.domain.QComment;

public interface QCommentRepository extends JpaRepository<QComment, Long> {

}
