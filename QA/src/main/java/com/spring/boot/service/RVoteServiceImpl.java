package com.spring.boot.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.boot.domain.RVote;
import com.spring.boot.repository.RVoteRepository;

@Service
public class RVoteServiceImpl implements RVoteService {

	@Autowired
	private RVoteRepository rvoteRepository;

	@Override
	@Transactional
	public void removeRVote(Long id) {
		rvoteRepository.delete(id);
	}
	@Override
	public RVote getRVoteById(Long id) {
		return rvoteRepository.findOne(id);
	}

}
