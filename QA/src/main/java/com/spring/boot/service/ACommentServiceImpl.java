package com.spring.boot.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.boot.domain.AComment;
import com.spring.boot.repository.ACommentRepository;

@Service
public class ACommentServiceImpl implements ACommentService {

	@Autowired
	private ACommentRepository acommentRepository;
	/* (non-Javadoc)
	 * @see com.waylau.spring.boot.blog.service.CommentService#removeComment(java.lang.Long)
	 */
	@Override
	@Transactional
	public void removeAComment(Long id) {
		acommentRepository.delete(id);
	}
	@Override
	public AComment getACommentById(Long id) {
		return acommentRepository.findOne(id);
	}

}
