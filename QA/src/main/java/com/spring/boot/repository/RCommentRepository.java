package com.spring.boot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.boot.domain.RComment;

public interface RCommentRepository extends JpaRepository<RComment, Long> {

}
