package com.spring.boot.repository.es;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.spring.boot.domain.es.EsResource;

public interface EsResourceRepository extends ElasticsearchRepository<EsResource, String> {
	
Page<EsResource> findDistinctEsResourceByTitleContainingOrSummaryContainingOrContentContainingOrTagsContaining(String title,String Summary,String content,String tags,Pageable pageable);
	
	EsResource findByResourceId(Long resourceId);

}
