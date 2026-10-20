package org.aryak.batch.processors;

import lombok.extern.slf4j.Slf4j;
import org.aryak.batch.archival.model.OutputRecord;
import org.aryak.batch.archival.transform.utils.JooqUtil;
import org.aryak.batch.model.InputRecord;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GenericProcessor implements ItemProcessor<InputRecord, OutputRecord> {

    @Override
    public OutputRecord process(@NonNull InputRecord item) throws Exception {

        
        log.info("In generic processor : {}", item);
        // mapping logic to output record, apply transformations here
        OutputRecord outputRecord = new OutputRecord();

        // populate the target object
        JooqUtil.setNestedProperty(outputRecord, "nested1.a", "");

        return outputRecord;
    }
}
