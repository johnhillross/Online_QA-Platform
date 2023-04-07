package com.spring.boot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.boot.domain.Vote;

public interface VoteRepository extends JpaRepository<Vote, Long> {

}
