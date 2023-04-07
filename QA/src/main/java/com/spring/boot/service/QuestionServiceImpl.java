package com.spring.boot.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.spring.boot.domain.Question;
import com.spring.boot.domain.Answer;
import com.spring.boot.domain.QComment;
import com.spring.boot.domain.User;
import com.spring.boot.domain.QVote;
import com.spring.boot.domain.es.EsQuestion;
import com.spring.boot.repository.QuestionRepository;

@Service
public class QuestionServiceImpl implements QuestionService {
	
	@Autowired
	private QuestionRepository questionRepository;
	@Autowired
	private EsQuestionService esQuestionService;

	@Transactional
	@Override
	public Question saveQuestion(Question question) {
		boolean isNew = (question.getId() == null);
		EsQuestion esQuestion = null;
		
		Question returnQuestion = questionRepository.save(question);
		
		if (isNew) {
			esQuestion = new EsQuestion(returnQuestion);
		} else {
			esQuestion = esQuestionService.getEsQuestionByQuestionId(question.getId());
			esQuestion.update(returnQuestion);
		}
		
		esQuestionService.updateEsQuestion(esQuestion);
		return returnQuestion;
	}

	@Transactional
	@Override
	public void removeQuestion(Long id) {
		questionRepository.delete(id);
		EsQuestion esquestion = esQuestionService.getEsQuestionByQuestionId(id);
		esQuestionService.removeEsQuestion(esquestion.getId());
	}

	@Override
	public Question getQuestionById(Long id) {
		return questionRepository.findOne(id);
	}

	@Override
	public Page<Question> listQuestionsByTitleQVote(User user, String title, Pageable pageable) {
		// 模糊查询
		title = "%" + title + "%";
		//Page<Question> questions = questionRepository.findByUserAndTitleLikeOrderByCreateTimeDesc(user, title, pageable);
		String tags = title;
		Page<Question> questions = questionRepository.findByTitleLikeAndUserOrTagsLikeAndUserOrderByCreateTimeDesc(title,user, tags,user, pageable);
		return questions;
	}

	@Override
	public Page<Question> listQuestionsByTitleQVoteAndSort(User user, String title, Pageable pageable) {
		// 模糊查询
		title = "%" + title + "%";
		Page<Question> questions = questionRepository.findByUserAndTitleLike(user, title, pageable);
		return questions;
	}
	
	@Override
	public Page<Question> listQuestionsByState(User user, String state, Pageable pageable) {
		
		boolean accState = false;
		
		if (state.equals("reSolved")) {
			accState = true;
		}
		
		Page<Question> questions = questionRepository.findByUserAndAccState(user, accState, pageable);
		return questions;
	}

	@Override
	public void viewingIncrease(Long id) {
		Question question = questionRepository.findOne(id);
		question.setViewSize(question.getViewSize()+1);
		this.saveQuestion(question);
	}

	@Override
	public Question createQComment(Long questionId, String qcommentContent) {
		Question originalQuestion = questionRepository.findOne(questionId);
		User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
		QComment qcomment = new QComment(user, qcommentContent);
		originalQuestion.addQComment(qcomment);
		return this.saveQuestion(originalQuestion);
	}

	@Override
	public void removeQComment(Long questionId, Long qcommentId) {
		Question originalQuestion = questionRepository.findOne(questionId);
		originalQuestion.removeQComment(qcommentId);
		this.saveQuestion(originalQuestion);
	}
	
	@Override
	public Question createAnswer(Long questionId, String answerContent) {
		Question originalQuestion = questionRepository.findOne(questionId);
		User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
		Answer answer = new Answer(user, answerContent);
		originalQuestion.addAnswer(answer);
		return this.saveQuestion(originalQuestion);
	}

	@Override
	public void removeAnswer(Long questionId, Long answerId) {
		Question originalQuestion = questionRepository.findOne(questionId);
		originalQuestion.removeAnswer(answerId);
		this.saveQuestion(originalQuestion);
	}

	@Override
	public Question createQUpVote(Long questionId) {
		Question originalQuestion = questionRepository.findOne(questionId);
		User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
		QVote qvote = new QVote(user);
		boolean isExist = originalQuestion.addQUpVote(qvote);
		if (isExist) {
			throw new IllegalArgumentException("该用户已经点过赞了");
		}
		return this.saveQuestion(originalQuestion);
	}

	@Override
	public void removeQUpVote(Long questionId, Long qvoteId) {
		Question originalQuestion = questionRepository.findOne(questionId);
		originalQuestion.removeQUpVote(qvoteId);
		this.saveQuestion(originalQuestion);
	}
	
	@Override
	public Question createQDownVote(Long questionId) {
		Question originalQuestion = questionRepository.findOne(questionId);
		User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
		QVote qvote = new QVote(user);
		boolean isExist = originalQuestion.addQDownVote(qvote);
		if (isExist) {
			throw new IllegalArgumentException("该用户已经点过赞了");
		}
		return this.saveQuestion(originalQuestion);
	}

	@Override
	public void removeQDownVote(Long questionId, Long qvoteId) {
		Question originalQuestion = questionRepository.findOne(questionId);
		originalQuestion.removeQDownVote(qvoteId);
		this.saveQuestion(originalQuestion);
	}

}
