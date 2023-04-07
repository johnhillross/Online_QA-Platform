package com.spring.boot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.boot.domain.Answer;
import com.spring.boot.domain.User;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
	
	@Deprecated
	Page<Answer> findByUserOrderByCreateTimeDesc(User user, Pageable pageable);
	
	Page<Answer> findByUser(User user, Pageable pageable);

}
