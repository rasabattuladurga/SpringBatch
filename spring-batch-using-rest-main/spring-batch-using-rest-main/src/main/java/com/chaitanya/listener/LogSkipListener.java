package com.chaitanya.listener;

import org.springframework.batch.core.annotation.OnSkipInRead;
import org.springframework.batch.item.file.FlatFileParseException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author ChaitanyaAnkam configuring annotation drive skip listener using Skip
 *         Listener annotations: @OnSkipInRead, @OnSkipInProcess,
 *         and @OnSkipInWrite.
 */
@Slf4j
public class LogSkipListener {

	@OnSkipInRead
	public void log(Throwable t) {
		if (t instanceof FlatFileParseException) {
			FlatFileParseException ffpe = (FlatFileParseException) t;
			log.info("Record skipped {}", ffpe.getInput(), ffpe.getLineNumber());
			//TODO save to database ("insert into skipped_records " + "(line,line_number) values (?,?)", ffpe.getInput(),ffpe.getLineNumber());
		}
	}
}
