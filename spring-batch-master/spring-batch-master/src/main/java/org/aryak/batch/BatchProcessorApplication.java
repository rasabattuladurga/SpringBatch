package org.aryak.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.HashMap;
import java.util.Map;

@EnableAsync
@SpringBootApplication
@EnableScheduling
@RequiredArgsConstructor
public class BatchProcessorApplication implements CommandLineRunner {

    private final Job studentJob;
    private final JobLauncher launcher;

    public static void main(String[] args) {
        SpringApplication.run(BatchProcessorApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Map<String, JobParameter<?>> parameters = new HashMap<>();
        parameters.put("k1", new JobParameter<>(System.currentTimeMillis(), Long.class));
        JobParameters p = new JobParameters(parameters);
        launcher.run(studentJob, p);
    }
}
