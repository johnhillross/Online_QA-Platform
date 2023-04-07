package com.spring.boot.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.spring.boot.domain.Resource;
import com.spring.boot.domain.RComment;
import com.spring.boot.domain.User;
import com.spring.boot.domain.RVote;
import com.spring.boot.domain.es.EsResource;
import com.spring.boot.repository.ResourceRepository;
import com.spring.boot.service.EsResourceService;

@Service
public class ResourceServiceImpl implements ResourceService {
	
	@Autowired
	private ResourceRepository resourceRepository;
	@Autowired
	private EsResourceService esResourceService;

	@Transactional
	@Override
	public Resource saveResource(Resource resource) {
		boolean isNew = (resource.getId() == null);
		EsResource esResource = null;
		
		Resource returnResource = resourceRepository.save(resource);
		
		if (isNew) {
			esResource = new EsResource(returnResource);
		} else {
			esResource = esResourceService.getEsResourceByResourceId(resource.getId());
			esResource.update(returnResource);
		}
		
		esResourceService.updateEsResource(esResource);
		return returnResource;
	}

	@Transactional
	@Override
	public void removeResource(Long id) {
		resourceRepository.delete(id);
		EsResource esresource = esResourceService.getEsResourceByResourceId(id);
		esResourceService.removeEsResource(esresource.getId());
	}

	@Override
	public Resource getResourceById(Long id) {
		return resourceRepository.findOne(id);
	}

	@Override
	public Page<Resource> listResourcesByTitleRVote(User user, String title, Pageable pageable) {
		// 模糊查询
		title = "%" + title + "%";
		//Page<Resource> resources = resourceRepository.findByUserAndTitleLikeOrderByCreateTimeDesc(user, title, pageable);
		String tags = title;
		Page<Resource> resources = resourceRepository.findByTitleLikeAndUserOrTagsLikeAndUserOrderByCreateTimeDesc(title,user, tags,user, pageable);
		return resources;
	}

	@Override
	public Page<Resource> listResourcesByTitleRVoteAndSort(User user, String title, Pageable pageable) {
		// 模糊查询
		title = "%" + title + "%";
		Page<Resource> resources = resourceRepository.findByUserAndTitleLike(user, title, pageable);
		return resources;
	}

	@Override
	public void rviewingIncrease(Long id) {
		Resource resource = resourceRepository.findOne(id);
		resource.setRviewSize(resource.getRviewSize()+1);
		this.saveResource(resource);
	}

	@Override
	public Resource createRComment(Long resourceId, String rcommentContent) {
		Resource originalResource = resourceRepository.findOne(resourceId);
		User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
		RComment rcomment = new RComment(user, rcommentContent);
		originalResource.addRComment(rcomment);
		return this.saveResource(originalResource);
	}

	@Override
	public void removeRComment(Long resourceId, Long rcommentId) {
		Resource originalResource = resourceRepository.findOne(resourceId);
		originalResource.removeRComment(rcommentId);
		this.saveResource(originalResource);
	}

	@Override
	public Resource createRVote(Long resourceId) {
		Resource originalResource = resourceRepository.findOne(resourceId);
		User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
		RVote rvote = new RVote(user);
		boolean isExist = originalResource.addRVote(rvote);
		if (isExist) {
			throw new IllegalArgumentException("该用户已经点过赞了");
		}
		return this.saveResource(originalResource);
	}

	@Override
	public void removeRVote(Long resourceId, Long rvoteId) {
		Resource originalResource = resourceRepository.findOne(resourceId);
		originalResource.removeRVote(rvoteId);
		this.saveResource(originalResource);
	}

}
