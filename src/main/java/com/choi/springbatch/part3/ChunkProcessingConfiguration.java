package com.choi.springbatch.part3;

import io.micrometer.core.instrument.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ChunkProcessingConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job chunkProcessingJob() {
        return jobBuilderFactory.get("chunkProcessingJob")
                .incrementer(new RunIdIncrementer())
                .start(this.taskBaseStep())
                .next(this.chunkBaseStep(null))
                .build();
    }

    @Bean
    public Step taskBaseStep() {
        return stepBuilderFactory.get("taskBaseStep")
                .tasklet(this.tasklet(null))
                .build();
    }

    @Bean
    @StepScope
    public Tasklet tasklet(@Value("#{jobParameters[chunkSize]}") String chunkSize) {
        return ((contribution, chunkContext) -> paging(contribution, chunkSize));
    }

    private RepeatStatus paging(StepContribution contribution, String value) {
        List<String> items = getItems();
        StepExecution stepExecution = contribution.getStepExecution();
//        JobParameters jobParameters = stepExecution.getJobParameters();
//        String value = jobParameters.getString("chunkSize", "10");
        int chunkSize = StringUtils.isNotBlank(value) ? Integer.parseInt(value) : 10;
        int fromIndex = stepExecution.getReadCount();
        int toIndex = fromIndex + chunkSize;
        if (fromIndex >= items.size()) {
            return RepeatStatus.FINISHED;
        }
        List<String> subList = items.subList(fromIndex, toIndex);
        log.info("task item size : {}", subList.size());
        stepExecution.setReadCount(toIndex);
        return RepeatStatus.CONTINUABLE;
    }

    // 100개의 데이터릘 10개씩 쪼개서
    @Bean
    @JobScope
    public Step chunkBaseStep(@Value("#{jobParameters[chunkSize]}") String chunkSize) {
        return stepBuilderFactory.get("chunkBaseStep")
                .<String, String>chunk(StringUtils.isNotBlank(chunkSize) ? Integer.parseInt(chunkSize) : 10)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .build();
    }

    private ItemReader<String> itemReader() {
        return new ListItemReader<>(getItems());
    }
    // item 의 데이터 가공
    // writer 넘길지 결정

    private ItemProcessor<? super String, String> itemProcessor() {
        return item -> item + ", Spring Batch";
    }

    private ItemWriter<? super String> itemWriter() {
//        return items -> items.forEach(log::info);
        return items -> log.info("chunk item size : {}", items.size());
    }

    private List<String> getItems() {
        return IntStream.range(0, 100)
                .mapToObj(index -> index + " Hello")
                .collect(Collectors.toList());
    }

}
