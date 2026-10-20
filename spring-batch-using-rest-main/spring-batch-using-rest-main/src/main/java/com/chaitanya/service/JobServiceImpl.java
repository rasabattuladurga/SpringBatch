package com.chaitanya.service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chaitanya.domain.model.ExecutionInfo;
import com.chaitanya.domain.model.JobRequest;
import com.chaitanya.domain.model.JobInfo;
import com.chaitanya.domain.model.JobStatus;
import com.chaitanya.exception.JobLaunchException;
import com.chaitanya.repository.BatchFileRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JobServiceImpl implements JobService {
	
	@Value("${app.batch.file.landing-zone}")
	private String fileLandingZone;
	
	@Autowired
	private Job job;
	
	@Autowired
	private SimpleJobOperator jobOperator;
	
	@Autowired
	private JobLauncher jobLauncher;
	
	@Autowired
	private JobExplorer jobExplorer;
	
	@Autowired
	private BatchFileRepository batchFileRepository;

	/**
	 * @implNote launchJob method implementation from JobService
	 * Launches spring-batch job using JobLauncher and Job classes
	 * */
	@Override
	public JobStatus launchJob(JobRequest jobDetails) throws JobLaunchException {
		try {
			//jobDetails.getParameters().put("startTime", AppUtils.currentTimestamp().toString());
			jobDetails.getParameters().put("filePath", fileLandingZone+"\\"+requiresFile(jobDetails.getFileId()));
			
			log.info("Launching Job..");
			JobParameters parameters = jobDetails.getJobParameters();
			JobExecution jobExecution = jobLauncher.run(job, parameters);
			
			JobStatus jobStatus = new JobStatus();
			jobStatus.setJodId(jobExecution.getJobId());
			jobStatus.setExeutionId(jobExecution.getId());
			return jobStatus;
		} catch(Exception exception) {
			throw new JobLaunchException("Unable to launch job ", exception);
		}
	}
	
	@Transactional(readOnly = true)
	private String requiresFile(String fileId) throws JobLaunchException {
		return batchFileRepository.findById(fileId)
			.map((batchFile)-> batchFile.getName())
			.orElseThrow(()->new JobLaunchException("File not uploaded yet"));
	}
	
	@Override
	public List<String> getJobNames() {
		return jobExplorer.getJobNames();
	}

	@Override
	public List<JobInfo> getJobInstances(String jobName, int start, int count) {
		return jobExplorer.getJobInstances(jobName, start, count)
			.stream()
			.sequential()
			.map(jobInstance -> convertToJobInfo(jobInstance))
			.collect(Collectors.toList());
	}

	@Override
	public JobStatus getLastJobExecution(long jobId, long exeutionId) {
		JobInstance instance = jobExplorer.getJobInstance(jobId);
		if(Objects.isNull(instance)) 
			return null;	
		JobExecution jobExecution = jobExplorer.getJobExecutions(instance).stream()
				.filter(exe->exe.getId()==exeutionId).findFirst().orElse(null);
		return convertToJobStatus(jobExecution);
	}
	

	@Override
	public JobStatus restart(long executionId) throws JobInstanceAlreadyCompleteException, NoSuchJobExecutionException, 
			NoSuchJobException, JobRestartException, JobParametersInvalidException {
		long newExecutionId = jobOperator.restart(executionId);
		JobExecution newExecution = jobExplorer.getJobExecution(newExecutionId);
		return convertToJobStatus(newExecution);
	}

	@Override
	public JobStatus stop(long executionId) throws NoSuchJobExecutionException, JobExecutionNotRunningException,
			JobLaunchException {
		boolean status = jobOperator.stop(executionId);
		if(!status)
			throw new JobLaunchException("Failed to stop job");
		return convertToJobStatus(jobExplorer.getJobExecution(executionId));
	}
	
	private List<String> getFailureExceptions(JobExecution jobExecution) {
		if (!jobExecution.getExitStatus().equals(ExitStatus.FAILED))
			return Collections.emptyList();
		Stream<String> jobFailuresStream =  jobExecution
				.getFailureExceptions()
				.stream()
				.map(Throwable::getMessage);
		Stream<String> stepFailuresStream = jobExecution.getStepExecutions()
				.stream()
				.map(se-> se.getFailureExceptions())
				.flatMap(List::stream)
				.map(Throwable::getMessage);
		return Stream.of(jobFailuresStream, stepFailuresStream).flatMap(Function.identity()).collect(Collectors.toList());
	}
	
	private JobInfo convertToJobInfo(JobInstance instance) {
		JobInfo jobInfo = new JobInfo();
		jobInfo.setJobname(instance.getJobName());
		jobInfo.setJobId(instance.getInstanceId());
		jobInfo.setExecutions(Collections.emptyList());
		
		List<JobExecution> executions = jobExplorer.getJobExecutions(instance);
		if(Objects.isNull(executions) || executions.isEmpty())
			return jobInfo;
		List<ExecutionInfo> executionInfoList = executions.stream().sequential()
		.map(execution -> {
			return ExecutionInfo.builder()
			.executionId(execution.getId())
			.status(execution.getStatus().name())
			.build();
		})
		.collect(Collectors.toList());
		jobInfo.setExecutions(executionInfoList);
		return jobInfo;
	}

	private JobStatus convertToJobStatus(JobExecution jobExecution) {
		if(Objects.isNull(jobExecution)) 
			return null;
		long count = jobExecution.getStepExecutions().stream().count();
		StepExecution stepExecution = jobExecution.getStepExecutions().stream()
				.sequential().skip(count - 1).findFirst().orElse(null);
		
		JobStatus jobstatus = new JobStatus();
		jobstatus.setExeutionId(jobExecution.getId());
		jobstatus.setJodId(jobExecution.getJobInstance().getInstanceId());
		jobstatus.setStartTime(jobExecution.getStartTime());
		jobstatus.setStatus(jobExecution.getStatus().name());	
		jobstatus.setEndTime(jobExecution.getEndTime());
		jobstatus.setFailureExceptions(getFailureExceptions(jobExecution));
		
		if(Objects.isNull(stepExecution)) 
			return jobstatus;
		jobstatus.setProcessed(stepExecution.getReadCount());
		jobstatus.setSkipped(stepExecution.getSkipCount());
		return jobstatus;
	}
	
}
