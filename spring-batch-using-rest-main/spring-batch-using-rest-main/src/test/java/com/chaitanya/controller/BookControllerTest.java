package com.chaitanya.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.chaitanya.domain.entity.Book;
import com.chaitanya.domain.entity.Tag;
import com.chaitanya.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = BookController.class)
public class BookControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BookService bookService;
	
	private Page<Book> page;
	private ObjectMapper mapper;
	
	@BeforeEach
	public void init() throws Exception {
		mapper = new ObjectMapper();
		
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
		when(bookService.findAll(Mockito.any(Pageable.class))).thenReturn(page);
		when(bookService.search(Mockito.anyString(), Mockito.any(Pageable.class))).thenReturn(page);
		when(bookService.findById(Mockito.anyString())).thenReturn(book);
	}

	@Test
	void findAll_thenReturn_PageWith200() throws Exception {
		MvcResult mvcResult = mockMvc.perform(get("/books/")
			    .contentType("application/json"))
				.andExpect(status().isOk())
			    .andReturn();
		String actualResponse = mvcResult.getResponse().getContentAsString();
		String expectedRespone = mapper.writeValueAsString(page);
		assertEquals(expectedRespone, actualResponse);
	}
	
	@Test
	void search_thenReturn_PageWith200() throws Exception {
		MvcResult mvcResult = mockMvc.perform(get("/books/search")
				.param("query", "ibsn=1001")
			    .contentType("application/json"))
				.andExpect(status().isOk())
			    .andReturn();
		String actualResponse = mvcResult.getResponse().getContentAsString();
		String expectedRespone = mapper.writeValueAsString(page);
		assertEquals(expectedRespone, actualResponse);
	}
	
	@Test
	void findById_thenReturn_PageWith200() throws Exception {
		MvcResult mvcResult = mockMvc.perform(get("/books/{ibsn}", "1001")
			    .contentType("application/json"))
				.andExpect(status().isOk())
			    .andReturn();
		String actualResponse = mvcResult.getResponse().getContentAsString();
		Book book = mapper.readValue(actualResponse, Book.class);
		assertEquals("1001", book.getIbsn());
	}
}
