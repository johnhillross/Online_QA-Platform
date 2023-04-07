package com.spring.boot.service;

import com.spring.boot.domain.QVote;

public interface QVoteService {
	
	QVote getQVoteById(Long id);
	
	void removeQVote(Long id);

}
