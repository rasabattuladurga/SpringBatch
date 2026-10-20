package com.chaitanya.exception;

import java.io.IOException;

import org.springframework.batch.core.JobExecutionException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.chaitanya.domain.model.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class RESTExceptionHandler extends ResponseEntityExceptionHandler {
	
	private static final String IO_ERROR_MESSAGE = "I/O PROCESSING ERROR";
	
	@ExceptionHandler(value = { IOException.class})
    protected ResponseEntity<ErrorResponse> handleIOIssues(Throwable ex) {
        return handleException(ex, IO_ERROR_MESSAGE, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
	
	@ExceptionHandler(value = { NotFoundException.class})
    protected ResponseEntity<ErrorResponse> handleResourceNotFound(Throwable ex) {
        return handleException(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(value = { FileImportExcepiton.class, SearchException.class, IllegalStateException.class })
    protected ResponseEntity<ErrorResponse> handleImportExceptions(Throwable ex) {
        return handleException(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(value = { JobLaunchException.class, JobExecutionException.class })
    protected ResponseEntity<ErrorResponse> handleJobExceptions(Throwable ex) {
        return handleException(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.PRECONDITION_FAILED);
    }
    
    private ResponseEntity<ErrorResponse> handleException(Throwable ex, String body, HttpHeaders headers, HttpStatus status) {
    	log.error("[REST Exception Handler]: ", ex);
    	ErrorResponse errorResponse = new ErrorResponse();
    	errorResponse.setHttpStatus(status.name());
    	errorResponse.setMessage(body);
    	return new ResponseEntity<ErrorResponse>(errorResponse, status);
    }
}
