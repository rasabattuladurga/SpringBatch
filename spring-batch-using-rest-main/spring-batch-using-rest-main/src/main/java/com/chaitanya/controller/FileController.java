package com.chaitanya.controller;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.chaitanya.domain.model.BatchFileInfo;
import com.chaitanya.exception.FileImportExcepiton;
import com.chaitanya.service.FileHandlingService;

import lombok.AllArgsConstructor;

/**
 * @author ChaitanyaAnkam
 * @apiNote FileController - File Management controller;
 *          Responsibilities: 
 *          a. Save the file uploaded 
 *          b. Expose the meta information/details of the uploaded files
 */
@RestController
@AllArgsConstructor
@RequestMapping(value = "/batchFile")
public class FileController {

	private FileHandlingService fileHandlingService;
	
	@PostMapping( value = "/upload", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public @ResponseBody ResponseEntity<BatchFileInfo> importFile(
			@RequestParam(value= "sourceSystemId", required = false, defaultValue = "UNKNOWN") String sourceSystemId,
			@RequestParam(value = "file", required = true) MultipartFile file) throws FileImportExcepiton {
		return new ResponseEntity<BatchFileInfo>(fileHandlingService.saveFile(file, sourceSystemId), HttpStatus.ACCEPTED);
	}
	
	@GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<List<BatchFileInfo>> findAll(
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size) throws Exception {
		Pageable pageable = PageRequest.of(page, size);
		return ResponseEntity.ok(fileHandlingService.findAll(pageable));
	}
}
