package org.aryak.batch.restart;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aryak.batch.archival.model.OutputRecord;
import org.aryak.batch.processors.EmployeeProcessor;
import org.aryak.batch.utils.RedisUtil;
import org.aryak.batch.writers.GenericWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class JobConfiguration {

    private final EmployeeProcessor processor;
    private final GenericWriter writer;
    private final RedisUtil redisUtil;

    @Bean
    @StepScope
    public FlatFileItemReader<Employee> reader() {
        FlatFileItemReader<Employee> reader = new FlatFileItemReader<>();
        var fileSystemResource = new FileSystemResource("/Users/aryak/Downloads/batch-processor/MOCK_DATA.csv");
        var filename = fileSystemResource.getFilename();
        reader.setResource(fileSystemResource);
        reader.setLineMapper(lineMapper());
        reader.setLinesToSkip(getStartPointForReading(filename));
        reader.setSaveState(false);
        reader.setName("csvReader");
        return reader;
    }

    private int getStartPointForReading(String filename) {

        int linesToSkipFromConfig = 1; // stubbed - will take value from config

        // case 2 : this is a file being retried
        if (redisUtil.getLastProcessedCount(filename) > 0) {
            return redisUtil.getLastProcessedCount(filename) + linesToSkipFromConfig;
        }

        // case 1 : file just started processing
        return linesToSkipFromConfig;

    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("student-step", jobRepository)
                .<Employee, OutputRecord>chunk(100, platformTransactionManager)
                .reader(reader())
                .processor(processor)
                .writer(writer)
                //.faultTolerant()
                //.skip(Exception.class)
                //.skipLimit(10)
                .build();
    }

    @Bean
    public Job studentJob(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new JobBuilder("student-job", jobRepository)
                .start(step1(jobRepository, platformTransactionManager))
                .build();

    }

    private LineMapper<Employee> lineMapper() {
        DefaultLineMapper<Employee> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(",");
        tokenizer.setNames("id", "first_name", "last_name", "email", "gender", "ip_address");
        tokenizer.setStrict(false);

        BeanWrapperFieldSetMapper<Employee> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Employee.class);

        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }


}
