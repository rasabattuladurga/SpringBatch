package com.chaitanya.steps;

import java.util.List;

import org.springframework.batch.item.support.AbstractItemStreamItemWriter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BookItemWriter<T> extends AbstractItemStreamItemWriter<T> {
	
	private JpaRepository<T, ?> jpaRepository;

	@Override
	@Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public void write(List<? extends T> books) throws Exception {
		jpaRepository.saveAll(books);
	}
}
