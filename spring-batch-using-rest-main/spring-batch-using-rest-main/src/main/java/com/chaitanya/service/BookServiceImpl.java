package com.chaitanya.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.chaitanya.domain.entity.Book;
import com.chaitanya.domain.entity.Tag;
import com.chaitanya.domain.model.SearchCriteria;
import com.chaitanya.exception.NotFoundException;
import com.chaitanya.exception.SearchException;
import com.chaitanya.repository.BookRepository;
import com.chaitanya.repository.specs.BookSpecification;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BookServiceImpl implements BookService {
	
	private BookRepository bookRepository;

	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
	public Page<Book> findAll(Pageable pageInfo) throws Exception {
		return bookRepository.findAll(pageInfo);
	}
	
	@Override
	public Page<Book> search(String query, Pageable pageInfo) throws Exception {
		Specification<Book> spec = getfilterSpecification(query);
		return (Objects.isNull(spec)) ? Page.empty(): bookRepository.findAll(spec, pageInfo);
	}

	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
	public Book findById(String id) throws Exception {
		return bookRepository.findById(id)
				.map(Function.identity())
				.orElseThrow(()->new NotFoundException
						(String.format("Resource Book with Id %s not found", id)));
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Book save(Book book) throws Exception {
		return bookRepository.save(book);
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Book update(Book book) throws Exception {
		Book bookFromDB = findById(book.getIbsn());
		bookFromDB.setName(book.getName());
		bookFromDB.setAuthor(book.getAuthor());
		
		Set<Tag> tagsFromDB = bookFromDB.getTags();
		if(Objects.nonNull(tagsFromDB))
			tagsFromDB.addAll(book.getTags());
		else 
			bookFromDB.setTags(book.getTags());
		return bookRepository.saveAndFlush(bookFromDB);
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void delete(String ibsn) throws Exception {
		bookRepository.delete(findById(ibsn));		
	}
	
	/**
	 * getfilterSpecification - is used to construct where clause
	 * from search parameters which are actual filters for search.
	 * @throws SearchException 
	 * */
	@Override
	public Specification<Book> getfilterSpecification(String query) throws SearchException {
		List<SearchCriteria> params = getSearchParams(query);
		if(Objects.isNull(params) || params.isEmpty())
			return null;
		
		List<Specification<Book>> specs = params.stream()
				.map(BookSpecification::new).collect(Collectors.toList());
		Specification<Book> filterSpec = specs.get(0);
		for (int i = 1; i < params.size(); i++) 
			filterSpec = Specification.where(filterSpec).and(specs.get(i));
		return filterSpec;		
	}
	
	private List<SearchCriteria> getSearchParams(String query) throws SearchException {
		List<SearchCriteria> params = new ArrayList<>();
		String[] queries = query.split(Pattern.quote(","));
		for(String q: queries) {
			String[] p = q.split(Pattern.quote(":"));
			if(p.length!=2)
				throw new SearchException("Invalid query");
			params.add(new SearchCriteria(p[0], ":", p[1]));
		}
		return params;		
	}
}
