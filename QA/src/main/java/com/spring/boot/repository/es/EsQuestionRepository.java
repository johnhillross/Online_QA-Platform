package com.spring.boot.repository.es;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.spring.boot.domain.es.EsQuestion;

public interface EsQuestionRepository extends ElasticsearchRepository<EsQuestion, String> {
	
	Page<EsQuestion> findDistinctEsQuestionByTitleContainingOrSummaryContainingOrContentContaining(String title,String Summary,String content,Pageable pageable);
	
	EsQuestion findByQuestionId(Long questionId);

}
