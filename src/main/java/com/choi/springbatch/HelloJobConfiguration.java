package com.choi.springbatch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class HelloJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;


//    @Bean
    public Job helloJob() {
        return jobBuilderFactory.get("helloJob")
                .start(helloStep01())
                .next(helloStep02())
                .build();
    }

//    @Bean
    public Step helloStep01() {
        return stepBuilderFactory.get("helloStep01")
                .tasklet((contribution, chunkContext) -> {
                    log.info("-----------------------------");
                    log.info("helloStep01 was executed!!");
                    log.info("-----------------------------");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

//    @Bean
    public Step helloStep02() {
        return stepBuilderFactory.get("helloStep02")
                .tasklet((contribution, chunkContext) -> {
                    log.info("-----------------------------");
                    log.info("helloStep02 was executed!!");
                    log.info("-----------------------------");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

}
