package com.chaitanya.service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.chaitanya.domain.entity.Tag;
import com.chaitanya.repository.TagRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class TagServiceImpl implements TagService {
	
	private TagRepository tagRepository;

	@Override
	@Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public Set<Tag> saveTagsIfNotExists(Set<String> tags) throws Exception {
		return tags
				.stream()
				.map(tag -> {
					Optional<Tag> tagFromDB = tagRepository.findByName(tag);
					if(tagFromDB.isPresent())
						return tagFromDB.get();
					else {
						Tag newTag = new Tag();
						newTag.setName(tag);
						log.info("adding new tag {} to database", tag);
						return tagRepository.save(newTag);
					}				
				})
				.collect(Collectors.toSet());
	}

	
}
