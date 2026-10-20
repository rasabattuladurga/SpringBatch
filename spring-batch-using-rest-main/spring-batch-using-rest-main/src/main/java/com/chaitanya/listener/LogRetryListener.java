package com.chaitanya.listener;

import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.listener.RetryListenerSupport;

import lombok.extern.slf4j.Slf4j;

/**
 * @author ChaitanyaAnkam
 * Logging Retry operations here
 */
@Slf4j
public class LogRetryListener extends RetryListenerSupport {

	@Override
	public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback,
			Throwable throwable) {
		log.error("Retried operation", throwable);
	}
}