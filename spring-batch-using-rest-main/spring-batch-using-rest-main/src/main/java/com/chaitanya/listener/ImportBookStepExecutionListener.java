package com.chaitanya.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImportBookStepExecutionListener implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution){
        log.info("ImportBookStepExecutionListener - before step execution.");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("ImportBookStepExecutionListener - after step execution.");
        return ExitStatus.COMPLETED;
    }
}
