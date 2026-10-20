package com.chaitanya.service;

import java.util.Set;

import com.chaitanya.domain.entity.Tag;

public interface TagService {

	Set<Tag> saveTagsIfNotExists(Set<String> tags) throws Exception ;
}
