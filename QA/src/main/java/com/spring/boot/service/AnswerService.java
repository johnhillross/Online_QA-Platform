package com.spring.boot.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.spring.boot.domain.Answer;
import com.spring.boot.domain.User;

public interface AnswerService {
	
	Answer saveAnswer(Answer answer);

	void removeAnswer(Long id);

	Answer getAnswerById(Long id);

	Page<Answer> listAnswersByAVote(User user, Pageable pageable);

	Page<Answer> listAnswersByAVoteAndSort(User suser, Pageable pageable);

	Answer createAComment(Long answerId, String answerContent);

	void removeAComment(Long answerId, Long acommentId);

	Answer createAUpVote(Long answerId);

	void removeAUpVote(Long answerId, Long avoteId);
	
	Answer createADownVote(Long answerId);

	void removeADownVote(Long answerId, Long avoteId);

}
