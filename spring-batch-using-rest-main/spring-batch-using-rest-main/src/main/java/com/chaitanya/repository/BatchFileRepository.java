package com.chaitanya.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.chaitanya.domain.entity.BatchFile;

@Repository
public interface BatchFileRepository extends JpaRepository<BatchFile, String> {

}
