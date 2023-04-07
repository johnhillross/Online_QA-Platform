package com.spring.boot.service;

import com.spring.boot.domain.RComment;

public interface RCommentService {
	
	RComment getRCommentById(Long id);
	
	void removeRComment(Long id);
	
}
