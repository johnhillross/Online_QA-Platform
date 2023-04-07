package com.spring.boot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.boot.domain.Question;
import com.spring.boot.domain.User;

public interface QuestionRepository extends JpaRepository<Question, Long> {
	
	@Deprecated
	Page<Question> findByUserAndTitleLikeOrderByCreateTimeDesc(User user, String title, Pageable pageable);
	
	Page<Question> findByUserAndTitleLike(User user, String title, Pageable pageable);
	
	Page<Question> findByTitleLikeAndUserOrTagsLikeAndUserOrderByCreateTimeDesc(String title,User user,String tags,User user2,Pageable pageable);
	
	Page<Question> findByUserAndAccState(User user, boolean accState, Pageable pageable);

}
