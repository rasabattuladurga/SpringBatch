package com.chaitanya.listener;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImportBookJobExecutionListener implements JobExecutionListener {

    public void beforeJob(JobExecution jobExecution) {
        log.info("ImportBookJobExecutionListener - beforeJob starting job..");
    }

    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED)
            log.info("ImportBookJobExecutionListener - job completed successfully");
        else if (jobExecution.getStatus() == BatchStatus.FAILED)
            log.info("ImportBookJobExecutionListener - job failed.");
    }
}
