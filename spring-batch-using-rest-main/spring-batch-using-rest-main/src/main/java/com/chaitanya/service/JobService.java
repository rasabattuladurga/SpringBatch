package com.chaitanya.service;

import java.util.List;

import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;

import com.chaitanya.domain.model.JobRequest;
import com.chaitanya.domain.model.JobInfo;
import com.chaitanya.domain.model.JobStatus;
import com.chaitanya.exception.JobLaunchException;

public interface JobService {

	/**
	 * @implSpec launchJob - method for starting a batch job
	 */
	JobStatus launchJob(JobRequest jobDetails) throws JobLaunchException;

	List<String> getJobNames();

	List<JobInfo> getJobInstances(String jobName, int start, int count);

	JobStatus getLastJobExecution(long jobId, long executionId);

	JobStatus restart(long executionId) throws JobInstanceAlreadyCompleteException, NoSuchJobExecutionException, 
			NoSuchJobException, JobRestartException, JobParametersInvalidException;
	
	JobStatus stop(long executionId) throws NoSuchJobExecutionException, JobExecutionNotRunningException,
			JobLaunchException;
}
