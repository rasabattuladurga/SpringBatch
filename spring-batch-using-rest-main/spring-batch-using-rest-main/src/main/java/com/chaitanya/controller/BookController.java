package com.chaitanya.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.chaitanya.domain.entity.Book;
import com.chaitanya.domain.model.GenericReponse;
import com.chaitanya.service.BookService;

import lombok.AllArgsConstructor;

/**
 * @author ChaitanyaAnkam
 * @apiNote Responsibilities:
 * 		    a. CRUD Operations on Book
 * 		    b. Search API with Filtering and Paging Support; TODO Adding Sorting and Projection support
 * */
@RestController
@RequestMapping(value = "/books")
@AllArgsConstructor
public class BookController {
	
	private BookService bookService;

	@GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<Page<Book>> findAll(
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size) throws Exception {
		Pageable pageable = PageRequest.of(page, size);
		return ResponseEntity.ok(bookService.findAll(pageable));
	}
	
	@GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<Page<Book>> search(
			@RequestParam(value="query", required = true, defaultValue ="ibsn:1001,author:Morgan Housel") String query,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size) throws Exception {
		Pageable pageable = PageRequest.of(page, size);
		return ResponseEntity.ok(bookService.search(query, pageable));
	}
	
	@GetMapping(value = "/{ibsn}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<Book> findById(
			@RequestParam(value="ibsn", defaultValue = "1001") String ibsn) throws Exception {
		return ResponseEntity.ok(bookService.findById(ibsn));
	}
	
	@PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE , produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<GenericReponse> create(@RequestBody Book newBook) throws Exception {
		newBook = bookService.save(newBook);
		GenericReponse response = GenericReponse.builder().message(String.format("created successfully"))
		.httpStatus(HttpStatus.CREATED.name()).build();
		return new ResponseEntity<GenericReponse>(response, HttpStatus.CREATED);
	}
	
	@PutMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE , produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<GenericReponse> update(@RequestBody Book updatedBook) throws Exception {
		updatedBook = bookService.update(updatedBook);
		GenericReponse response = GenericReponse.builder().message(String.format("updated successfully"))
				.httpStatus(HttpStatus.ACCEPTED.name()).build();
		return new ResponseEntity<GenericReponse>(response, HttpStatus.ACCEPTED);
	}
	
	@DeleteMapping(value = "/{ibsn}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<GenericReponse> delete(@RequestParam("ibsn") String ibsn) throws Exception {
		bookService.delete(ibsn);
		GenericReponse response = GenericReponse.builder().message(String.format("deleted successfully"))
				.httpStatus(HttpStatus.OK.name()).build();
		return new ResponseEntity<GenericReponse>(response, HttpStatus.OK);
	}
	
}
