package com.chaitanya.service;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.chaitanya.exception.FileImportExcepiton;

import lombok.extern.slf4j.Slf4j;

/**
 * @author ChaitanyaAnkam
 * {@summary} File System operations are handled
 * */
@Slf4j
@Service(value = "fileSystemStorageService")
public class FileSystemStorageServiceImpl implements FileStorageService {

	@Override
	public boolean fileExists(String path, String... rest) throws IOException {
		return Files.exists(Paths.get(path, rest));
	}

	@Override
	public void makeDir(String path) throws IOException {
		Files.createDirectories(Paths.get(path));		
	}

	@Override
	public void makeDirIfNotExists(String path) throws IOException {
		if(!fileExists(path)) makeDir(path);		
	}

	@Override
	public void requiresNoFile(String path, String name) throws IOException, FileImportExcepiton {
		if(fileExists(path, name))
			throw new FileImportExcepiton(String.format("File with name %s already exists", name));
	}

	@Override
	public void copyFiletoDestination(MultipartFile file, String path) throws FileImportExcepiton {
		Path filepath = Paths.get(path, file.getOriginalFilename());
	    try (OutputStream os = Files.newOutputStream(filepath)) {
	        os.write(file.getBytes());
	    } catch (Exception e) {
			log.error("Exception",e);
			throw new FileImportExcepiton("Unable to save file to landing-zone", e);
		}
	}

	@Override
	public void deletePath(String path, String... name) throws IOException {
		Files.deleteIfExists(Paths.get(path, name));
	}

	@Override
	public void clean(String path) throws IOException {
		FileUtils.cleanDirectory(Paths.get(path).toFile());
	}

}
