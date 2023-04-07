package com.spring.boot.service;

import java.util.List;

import com.spring.boot.domain.Catalog;
import com.spring.boot.domain.User;

public interface CatalogService {

	Catalog saveCatalog(Catalog catalog);

	void removeCatalog(Long id);

	Catalog getCatalogById(Long id);

	List<Catalog> listCatalogs(User user);
}
