package com.chaitanya.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.chaitanya.domain.model.JobRequest;
import com.chaitanya.domain.model.JobInfo;
import com.chaitanya.domain.model.JobStatus;
import com.chaitanya.exception.JobLaunchException;
import com.chaitanya.service.JobService;

import lombok.AllArgsConstructor;

/**
 * @author ChaitanyaAnkam
 * {@summary} JobController is a REST API which expose Job management end-points
 *          Job management end-points : 
 *          a. Start a Batch Job 
 *          b. Stop a Batch Job
 *          c. Restart a Batch Job 
 *          d. Get the status and meta information of the batch 
 *             job executions (processed, skipped, etc. counts)
 */
@RestController
@AllArgsConstructor
@RequestMapping(value = "/job")
public class JobController {
	
	private JobService jobService;
	
	@PostMapping( value = "/start", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<JobStatus> launchJob(@RequestBody JobRequest jobDetails) throws JobLaunchException {
		return new ResponseEntity<JobStatus>(jobService.launchJob(jobDetails), HttpStatus.CREATED);
	}
	
	@GetMapping( value = "/names", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<List<String>> findAllJobNames() {
		return ResponseEntity.ok(jobService.getJobNames());
	}
	
	@GetMapping( value = "/instances/{jobName}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<List<JobInfo>> findAllInstances(@PathVariable("jobName") String jobName) {
		return ResponseEntity.ok(jobService.getJobInstances(jobName, 0, 10));
	}
	
	@GetMapping( value = "/{jobId}/execution/{executionId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<JobStatus> findExecutionStatus(@PathVariable("jobId") long jobId, @PathVariable("executionId") long executionId) {
		return ResponseEntity.ok(jobService.getLastJobExecution(jobId, executionId));
	}
	
	@GetMapping( value = "/stop/{executionId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<JobStatus> abortJob(@PathVariable("executionId") long executionId) throws Exception {
		return ResponseEntity.ok(jobService.stop(executionId));
	}
	
	@GetMapping( value = "/restart/{executionId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<JobStatus> restartJob(@PathVariable("executionId") long executionId) throws Exception {
		return ResponseEntity.ok(jobService.restart(executionId));
	}
}
