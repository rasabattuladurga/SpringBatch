package com.chaitanya.listener;


import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImportBookChunkListener implements ChunkListener {
	
	@Override
	public void beforeChunk(ChunkContext context) {
	}

	@Override
	public void afterChunk(ChunkContext context) {
		log.info("Processed count:");
		log.info("Read count {}", context.getStepContext().getStepExecution().getReadCount());
		log.info("Write count {}", context.getStepContext().getStepExecution().getWriteCount());
		
		log.info("Skipped count:");			
		log.info("Read skip count {}", context.getStepContext().getStepExecution().getReadSkipCount());
		log.info("Process skip count {}", context.getStepContext().getStepExecution().getProcessSkipCount());
		log.info("Write skip count {}", context.getStepContext().getStepExecution().getWriteSkipCount());
		log.info("Total skip count {}", context.getStepContext().getStepExecution().getSkipCount());	
		
		log.info("Commit & Rollback count:");
		log.info("Commit count {}", context.getStepContext().getStepExecution().getCommitCount());
		log.info("Rollback count {}", context.getStepContext().getStepExecution().getRollbackCount());
	}

	@Override
	public void afterChunkError(ChunkContext context) {
	}

    
}
