package com.chaitanya.steps.policy;

import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.NonTransientFlatFileException;

/**
 * @author ChaitanyaAnkam
 * External Skip policy
 * 
 * Currently not In use
 * */
public class BatchSkipPolicy implements SkipPolicy {

    private static final int MAX_SKIP_COUNT = 20;

    @Override
    public boolean shouldSkip(Throwable throwable, int skipCount) 
      throws SkipLimitExceededException {
        if ((throwable instanceof FlatFileParseException || throwable instanceof NonTransientFlatFileException)
        		&& skipCount < MAX_SKIP_COUNT) {
            return true;
        }
        return false;
    }
}