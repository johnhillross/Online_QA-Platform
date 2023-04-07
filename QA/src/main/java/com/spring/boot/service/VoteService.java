package com.spring.boot.service;

import com.spring.boot.domain.Vote;

public interface VoteService {
	
	Vote getVoteById(Long id);
	
	void removeVote(Long id);

}
