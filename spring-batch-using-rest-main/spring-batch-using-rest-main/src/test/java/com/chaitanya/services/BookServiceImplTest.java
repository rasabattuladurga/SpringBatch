package com.chaitanya.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.chaitanya.domain.entity.Book;
import com.chaitanya.domain.entity.Tag;
import com.chaitanya.repository.BookRepository;
import com.chaitanya.service.BookServiceImpl;

@RunWith(SpringJUnit4ClassRunner.class)
public class BookServiceImplTest {
	
	private BookServiceImpl bookService;
	
	private BookRepository bookRepository;
	
	private Page<Book> page;
	
	@BeforeEach
	public void init() {
		bookRepository = Mockito.mock(BookRepository.class);
		bookService = new BookServiceImpl(bookRepository);
		
		List<Book> books = new ArrayList<>();
		Book book = new Book();
		book.setName("Intellijent Investor");
		book.setAuthor("Benjamin Graham");
		book.setIbsn("1001");
		book.setRawtags("Investing|Educational");

		Set<Tag> tags = new HashSet<>();
		Tag tag1 = new Tag();
		tag1.setId(1);
		tag1.setName("Investing");
		Tag tag2 = new Tag();
		tag2.setId(1);
		tag2.setName("Educational");

		tags.add(tag1);
		tags.add(tag2);

		Pageable pageInfo = PageRequest.of(0, 1);
		page = new PageImpl<Book>(books, pageInfo, 1);		
		Mockito.when(bookRepository.findAll(Mockito.any(Pageable.class))).thenReturn(page);
	}
	
	@Test
	void findAll_thenReturn_Books() throws Exception {
		Pageable pageInfo = PageRequest.of(0, 1);
		Page<Book> pageReceived = bookService.findAll(pageInfo);
		Assert.assertEquals(page.getSize(), pageReceived.getSize());
	}
}
