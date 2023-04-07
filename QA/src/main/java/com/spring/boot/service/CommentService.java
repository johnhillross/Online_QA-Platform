package com.spring.boot.service;

import com.spring.boot.domain.Comment;

public interface CommentService {
	
	Comment getCommentById(Long id);
	
	void removeComment(Long id);

}
