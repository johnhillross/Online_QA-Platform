package com.spring.boot.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.boot.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	/**
	 * 根据用户名分页查询用户列表
	 * @param username
	 * @param pageable
	 * @return
	 */
	Page<User> findByUsernameLike(String username, Pageable pageable);
	/**
	 * 根据名称查询
	 * @param username
	 * @return
	 */
	User findByUsername(String username);
	/**
	 * 根据名称列表查询
	 * @param usernames
	 * @return
	 */
	List<User> findByUsernameIn(Collection<String> usernames);

}
