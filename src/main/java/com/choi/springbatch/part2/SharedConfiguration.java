package com.choi.springbatch.part2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * job 끼리는 데이터 공유가 가능하나
 * step 끼리는 데이터 공유 불가
 * */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class SharedConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job sharedBean() {
        return jobBuilderFactory.get("sharedJob")
                .incrementer(new RunIdIncrementer())
                .start(this.sharedStep1())
                .next(this.sharedStep2())
                .build();
    }

    @Bean
    public Step sharedStep1() {
        return stepBuilderFactory.get("sharedStep1")
                .tasklet(((contribution, chunkContext) -> {
                    // step execution
                    StepExecution stepExecution = contribution.getStepExecution();
                    ExecutionContext stepExecutionContext = stepExecution.getExecutionContext();
                    stepExecutionContext.putString("stepKey", "step execution context");

                    // job execution
                    JobExecution jobExecution = stepExecution.getJobExecution();
                    ExecutionContext jobExecutionContext = jobExecution.getExecutionContext();
                    jobExecutionContext.putString("jobKey", "job execution context");
                    JobParameters jobParameters = jobExecution.getJobParameters();

                    // job instance
                    JobInstance jobInstance = jobExecution.getJobInstance();

                    log.info("jobName : {}, stepName : {}, parameter : {}",
                            jobInstance.getJobName(),
                            stepExecution.getStepName(),
                            jobParameters.getLong("run.id"));
                    return RepeatStatus.FINISHED;
                }))
                .build();
    }

    @Bean
    public Step sharedStep2() {
        return stepBuilderFactory.get("sharedStep2")
                .tasklet(((contribution, chunkContext) -> {
                    // step execution
                    StepExecution stepExecution = contribution.getStepExecution();
                    ExecutionContext stepExecutionContext = stepExecution.getExecutionContext();

                    // job execution
                    JobExecution jobExecution = stepExecution.getJobExecution();
                    ExecutionContext jobExecutionContext = jobExecution.getExecutionContext();

                    log.info("stepKey : {}, jobKey : {}",
                            stepExecutionContext.getString("stepKey", "emptyStepKey"),
                            jobExecutionContext.getString("jobKey", "emptyJobKey"));
                    return RepeatStatus.FINISHED;
                }))
                .build();
    }
    // stepKey : emptyStepKey, jobKey : job execution context
    // -> step 은 데이터 공유가 불가능하기 떄문

}
