package com.spring.boot.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.spring.boot.domain.Answer;
import com.spring.boot.domain.AComment;
import com.spring.boot.domain.AVote;
import com.spring.boot.domain.User;
import com.spring.boot.repository.AnswerRepository;

@Service
public class AnswerServiceImpl implements AnswerService {

	@Autowired
	private AnswerRepository answerRepository;

	@Transactional
	@Override
	public Answer saveAnswer(Answer answer) {
		Answer returnAnswer = answerRepository.save(answer);
		return returnAnswer;
	}

	@Transactional
	@Override
	public void removeAnswer(Long id) {
		answerRepository.delete(id);
	}

	@Override
	public Answer getAnswerById(Long id) {
		return answerRepository.findOne(id);
	}

	@Override
	public Page<Answer> listAnswersByAVote(User user, Pageable pageable) {
		
		//Page<Answer> answers = answerRepository.findByUserAndTitleLikeOrderByCreateTimeDesc(user, title, pageable);
		Page<Answer> answers = answerRepository.findByUserOrderByCreateTimeDesc(user, pageable);
		return answers;
	}

	@Override
	public Page<Answer> listAnswersByAVoteAndSort(User user, Pageable pageable) {
		// 模糊查询
		Page<Answer> answers = answerRepository.findByUser(user, pageable);
		return answers;
	}

	@Override
	public Answer createAComment(Long answerId, String acommentContent) {
		Answer originalAnswer = answerRepository.findOne(answerId);
		User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
		AComment acomment = new AComment(user, originalAnswer, acommentContent);
		originalAnswer.addAComment(acomment);
		return this.saveAnswer(originalAnswer);
	}

	@Override
	public void removeAComment(Long answerId, Long acommentId) {
		Answer originalAnswer = answerRepository.findOne(answerId);
		originalAnswer.removeAComment(acommentId);
		this.saveAnswer(originalAnswer);
	}

	@Override
	public Answer createAUpVote(Long answerId) {
		Answer originalAnswer = answerRepository.findOne(answerId);
		User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
		AVote avote = new AVote(user, originalAnswer);
		boolean isExist = originalAnswer.addAUpVote(avote);
		if (isExist) {
			throw new IllegalArgumentException("该用户已经点过赞了");
		}
		return this.saveAnswer(originalAnswer);
	}

	@Override
	public void removeAUpVote(Long answerId, Long avoteId) {
		Answer originalAnswer = answerRepository.findOne(answerId);
		originalAnswer.removeAUpVote(avoteId);
		this.saveAnswer(originalAnswer);
	}
	
	@Override
	public Answer createADownVote(Long answerId) {
		Answer originalAnswer = answerRepository.findOne(answerId);
		User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
		AVote avote = new AVote(user, originalAnswer);
		boolean isExist = originalAnswer.addADownVote(avote);
		if (isExist) {
			throw new IllegalArgumentException("该用户已经点过赞了");
		}
		return this.saveAnswer(originalAnswer);
	}

	@Override
	public void removeADownVote(Long answerId, Long avoteId) {
		Answer originalAnswer = answerRepository.findOne(answerId);
		originalAnswer.removeADownVote(avoteId);
		this.saveAnswer(originalAnswer);
	}

}
