package com.spring.boot.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.spring.boot.domain.User;
import com.spring.boot.domain.es.EsResource;
import com.spring.boot.vo.TagVO;

public interface EsResourceService {
	
	void removeEsResource(String id);

	EsResource updateEsResource(EsResource esResource);

	EsResource getEsResourceByResourceId(Long resourceId);

	Page<EsResource> listNewestEsResources(String keyword, Pageable pageable);

	Page<EsResource> listHotestEsResources(String keyword, Pageable pageable);

	Page<EsResource> listEsResources(Pageable pageable);

	List<EsResource> listTop5NewestEsResources();

	List<EsResource> listTop5HotestEsResources();

	List<TagVO> listTop30Tags();

	List<User> listTop12Users();

}
