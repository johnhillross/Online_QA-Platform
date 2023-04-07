package com.spring.boot.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.boot.domain.RComment;
import com.spring.boot.repository.RCommentRepository;

@Service
public class RCommentServiceImpl implements RCommentService {

	@Autowired
	private RCommentRepository rcommentRepository;
	/* (non-Javadoc)
	 * @see com.waylau.spring.boot.blog.service.RCommentService#removeRComment(java.lang.Long)
	 */
	@Override
	@Transactional
	public void removeRComment(Long id) {
		rcommentRepository.delete(id);
	}
	@Override
	public RComment getRCommentById(Long id) {
		return rcommentRepository.findOne(id);
	}

}
