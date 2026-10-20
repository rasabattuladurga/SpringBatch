package com.chaitanya.steps;

import java.util.Arrays;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.batch.item.ItemProcessor;

import com.chaitanya.domain.entity.Book;
import com.chaitanya.domain.entity.Tag;
import com.chaitanya.service.TagService;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Setter
@Getter
public class BookProcessor implements ItemProcessor<Book, Book> {
	
	private TagService tagService;
	
	private static final String TAGS_DELIMITER = "|";

	@Override
	public Book process(Book book) throws Exception {
		Set<String> tagLiterals = Arrays
				.stream(book.getRawtags().split(Pattern.quote(TAGS_DELIMITER)))
				.collect(Collectors.toSet());
		Set<Tag> tagsFromDB =  tagService.saveTagsIfNotExists(tagLiterals);
		book.setTags(tagsFromDB);
		log.info("Successfully Added Tags for Book {}", book.getName(), book.getRawtags());
		return book;
	}
}
