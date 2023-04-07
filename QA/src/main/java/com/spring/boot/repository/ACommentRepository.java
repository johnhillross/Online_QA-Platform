package com.spring.boot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.boot.domain.AComment;

public interface ACommentRepository extends JpaRepository<AComment, Long> {

}
