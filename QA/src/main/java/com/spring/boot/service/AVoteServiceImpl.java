package com.spring.boot.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.boot.domain.AVote;
import com.spring.boot.repository.AVoteRepository;

@Service
public class AVoteServiceImpl implements AVoteService {

	@Autowired
	private AVoteRepository avoteRepository;

	@Override
	@Transactional
	public void removeAVote(Long id) {
		avoteRepository.delete(id);
	}
	@Override
	public AVote getAVoteById(Long id) {
		return avoteRepository.findOne(id);
	}

}
