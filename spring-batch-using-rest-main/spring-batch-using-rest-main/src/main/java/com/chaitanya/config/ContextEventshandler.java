package com.chaitanya.config;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.chaitanya.service.FileHandlingService;

import lombok.AllArgsConstructor;

/**
 * @author ChaitanyaAnkam
 * {@summary} Added to handle application context events
 * 
 * */
@Lazy(false)
@Component
@AllArgsConstructor
public class ContextEventshandler {
	
	private FileHandlingService fileHandlingService;

	/**
	 * {@summary} cleaning up file landing-zone on context start and stop
	 * */
	@EventListener(classes = { ContextStartedEvent.class, ContextStoppedEvent.class, ContextRefreshedEvent.class })
	public void handleMultipleEvents() throws Exception {
		fileHandlingService.cleanup();
	}
}
