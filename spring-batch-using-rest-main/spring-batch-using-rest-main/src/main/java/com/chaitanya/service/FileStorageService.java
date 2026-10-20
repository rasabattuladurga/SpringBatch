package com.chaitanya.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.chaitanya.exception.FileImportExcepiton;

/**
 * @author ChaitanyaAnkam 
 * {@summary} on Storage Service
 * 		   Generic Service definition to handle File Storage
 * 		   More Implementations for the same can be added
 */
public interface FileStorageService {
	
	enum STORAGE {FILE_SYSTEM, NAS, OTHER};

	boolean fileExists(String path, String... rest) throws IOException;
	
	void makeDir(String path) throws IOException;
	
	void makeDirIfNotExists(String path) throws IOException;
	
	void requiresNoFile(String path, String name) throws IOException, FileImportExcepiton;
	
	void copyFiletoDestination(MultipartFile file, String path) throws FileImportExcepiton;
	
	void deletePath(String path, String... name) throws IOException;
	
	void clean(String path) throws IOException;
}
