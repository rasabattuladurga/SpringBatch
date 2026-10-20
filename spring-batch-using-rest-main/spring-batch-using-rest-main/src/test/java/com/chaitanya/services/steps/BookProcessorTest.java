package com.chaitanya.services.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.chaitanya.domain.entity.Book;
import com.chaitanya.domain.entity.Tag;
import com.chaitanya.service.TagService;
import com.chaitanya.service.TagServiceImpl;
import com.chaitanya.steps.BookProcessor;

@RunWith(SpringJUnit4ClassRunner.class)
public class BookProcessorTest {
	
	private BookProcessor processor;
	
	private TagService tagService;

	@BeforeEach
	public void init() throws Exception {
		processor = new BookProcessor();
		tagService = Mockito.mock(TagServiceImpl.class);
		processor.setTagService(tagService);		
		
		Set<Tag> tags = new HashSet<>();
		Tag tag1 = new Tag();
		tag1.setId(1);
		tag1.setName("Investing");
		Tag tag2 = new Tag();
		tag2.setId(1);
		tag2.setName("Educational");

		tags.add(tag1);
		tags.add(tag2);

		when(tagService.saveTagsIfNotExists(Mockito.anySet())).thenReturn(tags);		
	}
	
	@Test
	void process_then_addTags() throws Exception {
		Book book = new Book();
		book.setName("Intellijent Investor");
		book.setAuthor("Benjamin Graham");
		book.setIbsn("1001");
		book.setRawtags("Investing|Educational");
		
		processor.process(book);
		assertEquals(book.getTags().size(), 2);
	}
	
}
