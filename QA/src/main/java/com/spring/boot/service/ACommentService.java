package com.spring.boot.service;

import com.spring.boot.domain.AComment;

public interface ACommentService {
	
	AComment getACommentById(Long id);
	
	void removeAComment(Long id);

}
