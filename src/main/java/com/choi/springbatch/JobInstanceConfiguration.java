package com.choi.springbatch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class JobInstanceConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job job() {
        return jobBuilderFactory.get("job")
                .start(step01())
                .next(step02())
                .build();
    }

    private Step step01() {
        return stepBuilderFactory.get("step01")
                .tasklet((contribution, chunkContext) -> {
                    JobParameters jobParameters = contribution.getStepExecution().getJobExecution().getJobParameters();
                    jobParameters.getString("name");
                    jobParameters.getLong("seq");
                    jobParameters.getDate("date");
                    jobParameters.getDate("age");
                    Map<String, Object> parameters = chunkContext.getStepContext().getJobParameters();
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    private Step step02() {
        return stepBuilderFactory.get("step02")
                .tasklet((contribution, chunkContext) -> RepeatStatus.FINISHED)
                .build();
    }

}
