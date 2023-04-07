package com.spring.boot.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.boot.domain.QComment;
import com.spring.boot.repository.QCommentRepository;

@Service
public class QCommentServiceImpl implements QCommentService {
	
	@Autowired
	private QCommentRepository qcommentRepository;
	/* (non-Javadoc)
	 * @see com.waylau.spring.boot.blog.service.CommentService#removeComment(java.lang.Long)
	 */
	@Override
	@Transactional
	public void removeQComment(Long id) {
		qcommentRepository.delete(id);
	}
	@Override
	public QComment getQCommentById(Long id) {
		return qcommentRepository.findOne(id);
	}

}
