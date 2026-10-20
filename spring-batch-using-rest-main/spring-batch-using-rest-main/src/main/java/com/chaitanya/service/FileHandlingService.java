package com.chaitanya.service;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.chaitanya.domain.entity.BatchFile;
import com.chaitanya.domain.model.BatchFileInfo;
import com.chaitanya.exception.FileImportExcepiton;

public interface FileHandlingService {
	
	BatchFileInfo saveFile(MultipartFile multipartFile, String sourceSystemId) throws FileImportExcepiton;

	void saveBatchFileInfo(BatchFile batchFile) throws IOException, FileImportExcepiton;

	void cleanup() throws Exception;
	
	List<BatchFileInfo> findAll(Pageable page) throws Exception;
}
