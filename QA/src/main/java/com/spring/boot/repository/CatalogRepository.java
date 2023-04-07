package com.spring.boot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.boot.domain.Catalog;
import com.spring.boot.domain.User;

public interface CatalogRepository extends JpaRepository<Catalog, Long> {
	
	List<Catalog> findByUser(User user);
	
	List<Catalog> findByUserAndName(User user,String name);

}
