package com.chaitanya.config;

import java.nio.file.Paths;

import javax.sql.DataSource;

import org.hibernate.dialect.lock.PessimisticEntityLockException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.item.ItemReaderException;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.FlatFileFormatException;
import org.springframework.batch.item.file.transform.IncorrectTokenCountException;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.item.support.SynchronizedItemStreamWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.transaction.PlatformTransactionManager;

import com.chaitanya.domain.entity.Book;
import com.chaitanya.listener.ImportBookChunkListener;
import com.chaitanya.listener.ImportBookJobExecutionListener;
import com.chaitanya.listener.ImportBookStepExecutionListener;
import com.chaitanya.listener.LogSkipListener;
import com.chaitanya.repository.BookRepository;
import com.chaitanya.service.TagService;
import com.chaitanya.steps.BookItemWriter;
import com.chaitanya.steps.BookProcessor;

import lombok.AllArgsConstructor;


/**
 * @author ChaitanyaAnkam 
 * {@summary} ApplBatchConfig - This class extends
 *         Spring Framework BatchConfigurer to create the following
 *         jobRepository, jobLauncher, jobExplorer, job, jobOperator And Book
 *         Job configuration, which requires: ImportJob, Import Steps
 *         (BookItemReader, BookItemProcessor, BookItemWriter) Althogh no
 *         processing task is not required, this batch configuration is
 *         extensible for book data enrichment
 * 
 *         Executor Service (parallel processing) use is commented due to following issue
 *         https://github.com/spring-projects/spring-batch/pull/591.
 *         The issue is internal to spring (OptimisticLockException using JobExecutionDao)
 *         while updating step execution.
 */
@Order(2)
@Configuration
@AllArgsConstructor
@EnableBatchProcessing
public class BatchConfig implements BatchConfigurer {
	
	private TagService tagService;
	
	private DataSource appDataSource;
	
	private BookRepository bookRepository;
	
	private JobBuilderFactory libJobBuilderFactory;
	
	private StepBuilderFactory libStepBuilderFactory;
	
	private PlatformTransactionManager appTransactionManager;
	
	
	@Override
	public JobRepository getJobRepository() throws Exception {
		JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
	    factory.setDataSource(appDataSource);
	    factory.setTransactionManager(getTransactionManager());
	    factory.setIsolationLevelForCreate("ISOLATION_SERIALIZABLE");
	    factory.setTablePrefix("BATCH_");
	    factory.setMaxVarCharLength(1000);
	    return factory.getObject();
	}

	@Override
	public PlatformTransactionManager getTransactionManager() throws Exception {
		return appTransactionManager;
	}

	@Override
	public JobLauncher getJobLauncher() throws Exception {
		SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
		jobLauncher.setJobRepository(getJobRepository());
		jobLauncher.setTaskExecutor(asyncTaskExecutor2());
		jobLauncher.afterPropertiesSet();
		return jobLauncher;
	}

	@Override
	public JobExplorer getJobExplorer() throws Exception {
		JobExplorerFactoryBean factory = new JobExplorerFactoryBean();
        factory.setDataSource(appDataSource);
        factory.afterPropertiesSet();
        factory.setTablePrefix("BATCH_");
		return factory.getObject();
	}
	
	@Bean
	public JobRegistry inMemoryjobRegistry() throws Exception {
		return new MapJobRegistry();
	}
	
	@Bean
	public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor() throws Exception {
	    JobRegistryBeanPostProcessor postProcessor = new JobRegistryBeanPostProcessor();
	    postProcessor.setJobRegistry(inMemoryjobRegistry());
	    return postProcessor;
	}
	
	@Bean
	public SimpleJobOperator jobOperator() throws Exception {
		SimpleJobOperator jobOperator = new SimpleJobOperator();
		jobOperator.setJobLauncher(getJobLauncher());
		jobOperator.setJobExplorer(getJobExplorer());
		jobOperator.setJobRegistry(inMemoryjobRegistry());
		jobOperator.setJobRepository(getJobRepository());
		jobOperator.afterPropertiesSet();
		return jobOperator;
	}
	
	@Bean
	public Job importBookJob() throws Exception {
		return libJobBuilderFactory
				.get("importBookJob")
				//.preventRestart() default spring-batch can be restarted
				.repository(getJobRepository())
				.incrementer(new RunIdIncrementer())
				.listener(new ImportBookJobExecutionListener())
				.flow(importBookStep())
                .end()
                .build();
	}

    @Bean
    public Step importBookStep() throws Exception {
        return libStepBuilderFactory
        		.get("importBookStep")
        		.repository(getJobRepository())
        		.<Book, Book>chunk(10)
        		.reader(bookItemReader(null))
                .processor(bookItemProcessor())
                .writer(bookItemWriter())
                .faultTolerant()
                .skipLimit(10)
                .skip(ItemReaderException.class)
                .skip(FlatFileFormatException.class)
                .skip(IncorrectTokenCountException.class)
                .noSkip(NonTransientResourceException.class)
                .retryLimit(3)
                .retry(TransientDataAccessException.class)
                .retry(DeadlockLoserDataAccessException.class)
                .noRetry(PessimisticEntityLockException.class)
                .allowStartIfComplete(true)
                .startLimit(2)
                .listener(new ImportBookStepExecutionListener())
                .listener(new ImportBookChunkListener())
                .listener(new LogSkipListener())
                //.taskExecutor(asyncTaskExecutor()) commenting to spring issue https://github.com/spring-projects/spring-batch/pull/591
                //.throttleLimit(10)
                .build();
    }

    @Bean
    @StepScope
    public SynchronizedItemStreamReader<Book> bookItemReader(@Value("#{jobParameters[filePath]}") String filePath) {
        FlatFileItemReader<Book> itemReader = new FlatFileItemReaderBuilder<Book>()
                .name("bookItemReader")
                .resource(new FileSystemResource(Paths.get(filePath)))
                .delimited()
                .delimiter(",")
                .names(new String[] {"ibsn", "name","author", "rawtags"})
                .targetType(Book.class)
                .build();
        SynchronizedItemStreamReader<Book> synchronizedItemStreamReader = new SynchronizedItemStreamReader<>();
        synchronizedItemStreamReader.setDelegate(itemReader);
        return synchronizedItemStreamReader;
    }

    @Bean
    public BookProcessor bookItemProcessor() {
	    BookProcessor processor = new BookProcessor();
	    processor.setTagService(tagService);
	    return processor;
    }

    @Bean
    public SynchronizedItemStreamWriter<Book> bookItemWriter() {
    	BookItemWriter<Book> bookWriter =  new BookItemWriter<Book>();
    	bookWriter.setName("bookItemWriter");
    	bookWriter.setJpaRepository(bookRepository);
    	SynchronizedItemStreamWriter<Book> synchronizedItemStreamWriter = new SynchronizedItemStreamWriter<>();
    	synchronizedItemStreamWriter.setDelegate(bookWriter);
    	return synchronizedItemStreamWriter;
    }    
    
    @Bean
    public TaskExecutor asyncTaskExecutor() {
        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        taskExecutor.setConcurrencyLimit(20);
        taskExecutor.setThreadPriority(1);
        taskExecutor.setThreadNamePrefix("import-job");
        return taskExecutor;
    }
    
    @Bean
    public TaskExecutor asyncTaskExecutor2() {
        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        taskExecutor.setConcurrencyLimit(5);
        taskExecutor.setThreadPriority(1);
        taskExecutor.setThreadNamePrefix("job-launcher");
        return taskExecutor;
    }
   
}
