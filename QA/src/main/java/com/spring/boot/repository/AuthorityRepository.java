package com.spring.boot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.boot.domain.Authority;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {

}
