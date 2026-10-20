package com.chaitanya.steps;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ItemCountItemStream implements ItemStream {
 
    public void open(ExecutionContext executionContext) throws ItemStreamException {
    }
 
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        log.info("#### Iteam Reader Current count: {} of {} ####", 
        		executionContext.get("FlatFileItemReader.read.count"),
        		executionContext.get("FlatFileItemReader.read.count.max"));
    }
 
    public void close() throws ItemStreamException {
    }
}