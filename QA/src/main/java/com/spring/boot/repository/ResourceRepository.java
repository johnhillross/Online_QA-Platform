package com.spring.boot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.boot.domain.Resource;
import com.spring.boot.domain.User;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
	
	@Deprecated
	Page<Resource> findByUserAndTitleLikeOrderByCreateTimeDesc(User user, String title, Pageable pageable);
	
	Page<Resource> findByUserAndTitleLike(User user, String title, Pageable pageable);
	
	Page<Resource> findByTitleLikeAndUserOrTagsLikeAndUserOrderByCreateTimeDesc(String title,User user,String tags,User user2,Pageable pageable);

}
