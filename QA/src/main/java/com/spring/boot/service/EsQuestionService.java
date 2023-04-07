package com.spring.boot.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.spring.boot.domain.User;
import com.spring.boot.domain.es.EsQuestion;
import com.spring.boot.vo.TagVO;

public interface EsQuestionService {
	
	void removeEsQuestion(String id);

	EsQuestion updateEsQuestion(EsQuestion esQuestion);

	EsQuestion getEsQuestionByQuestionId(Long questionId);

	Page<EsQuestion> listNewestEsQuestions(String keyword, Pageable pageable);

	Page<EsQuestion> listHotestEsQuestions(String keyword, Pageable pageable);

	Page<EsQuestion> listEsQuestions(Pageable pageable);

	List<EsQuestion> listTop5NewestEsQuestions();

	List<EsQuestion> listTop5HotestEsQuestions();

	List<TagVO> listTop30Tags();

	List<User> listTop12Users();

}
