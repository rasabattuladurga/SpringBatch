package com.chaitanya.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.chaitanya.domain.entity.Book;
import com.chaitanya.exception.SearchException;

public interface BookService {

	Page<Book> findAll(Pageable pageInfo) throws Exception;
	
	Page<Book> search(String query, Pageable pageInfo) throws Exception;
	
	Book findById(String id) throws Exception;
	
	Book save(Book book) throws Exception;
	
	Book update(Book book) throws Exception;
	
	void delete(String ibsn) throws Exception;
	
	Specification<Book> getfilterSpecification(String query) throws SearchException;
}
