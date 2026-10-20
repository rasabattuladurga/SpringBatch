package org.aryak.batch.processors;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aryak.batch.archival.model.OutputRecord;
import org.aryak.batch.restart.Employee;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class EmployeeProcessor implements ItemProcessor<Employee, OutputRecord> {

    @Override
    public OutputRecord process(@NonNull Employee item) throws Exception {
        //log.info("student : {}", item);
        return new OutputRecord();
    }
}
