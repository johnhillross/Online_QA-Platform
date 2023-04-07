package com.spring.boot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.boot.domain.QVote;

public interface QVoteRepository extends JpaRepository<QVote, Long> {

}
