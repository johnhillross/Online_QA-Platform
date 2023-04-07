package com.spring.boot.service;

import com.spring.boot.domain.QComment;

public interface QCommentService {
	
	QComment getQCommentById(Long id);
	
	void removeQComment(Long id);

}
