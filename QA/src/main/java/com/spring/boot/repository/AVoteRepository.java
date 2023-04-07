package com.spring.boot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.boot.domain.AVote;

public interface AVoteRepository extends JpaRepository<AVote, Long> {

}
