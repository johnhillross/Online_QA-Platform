package com.spring.boot.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.boot.domain.QVote;
import com.spring.boot.repository.QVoteRepository;

@Service
public class QVoteServiceImpl implements QVoteService {

	@Autowired
	private QVoteRepository qvoteRepository;

	@Override
	@Transactional
	public void removeQVote(Long id) {
		qvoteRepository.delete(id);
	}
	@Override
	public QVote getQVoteById(Long id) {
		return qvoteRepository.findOne(id);
	}

}
