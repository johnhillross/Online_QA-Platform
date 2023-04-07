package com.spring.boot.service;

import com.spring.boot.domain.RVote;

public interface RVoteService {
	
	RVote getRVoteById(Long id);
	
	void removeRVote(Long id);
	
}
