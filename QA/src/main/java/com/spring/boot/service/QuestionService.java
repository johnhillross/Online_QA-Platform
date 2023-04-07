package com.spring.boot.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.spring.boot.domain.Question;
import com.spring.boot.domain.User;

public interface QuestionService {
	
	Question saveQuestion(Question question);

	void removeQuestion(Long id);

	Question getQuestionById(Long id);

	Page<Question> listQuestionsByTitleQVote(User user, String title, Pageable pageable);

	Page<Question> listQuestionsByTitleQVoteAndSort(User user, String title, Pageable pageable);
	
	Page<Question> listQuestionsByState(User user, String state, Pageable pageable);

	void viewingIncrease(Long id);

	Question createQComment(Long questionId, String questionContent);

	void removeQComment(Long questionId, Long qcommentId);
	
	Question createAnswer(Long questionId, String questionContent);

	void removeAnswer(Long questionId, Long answerId);

	Question createQUpVote(Long questionId);

	void removeQUpVote(Long questionId, Long qvoteId);
	
	Question createQDownVote(Long questionId);

	void removeQDownVote(Long questionId, Long qvoteId);

}
