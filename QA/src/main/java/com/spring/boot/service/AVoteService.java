package com.spring.boot.service;

import com.spring.boot.domain.AVote;

public interface AVoteService {
	
	AVote getAVoteById(Long id);
	
	void removeAVote(Long id);

}
