package com.spring.boot.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.spring.boot.domain.Resource;
import com.spring.boot.domain.User;

public interface ResourceService {
	
	Resource saveResource(Resource resource);

	void removeResource(Long id);

	Resource getResourceById(Long id);

	Page<Resource> listResourcesByTitleRVote(User user, String title, Pageable pageable);

	Page<Resource> listResourcesByTitleRVoteAndSort(User suser, String title, Pageable pageable);

	void rviewingIncrease(Long id);

	Resource createRComment(Long resourceId, String resourceContent);

	void removeRComment(Long resourceId, Long rcommentId);

	Resource createRVote(Long resourceId);

	void removeRVote(Long resourceId, Long rvoteId);

}
