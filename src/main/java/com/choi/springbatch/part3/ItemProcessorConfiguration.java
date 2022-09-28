package com.choi.springbatch.part3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.choi.springbatch.part3.CreateItemUtils.idItem;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class ItemProcessorConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job itemProcessorJob() {
        return this.jobBuilderFactory.get("itemProcessorJob")
                .incrementer(new RunIdIncrementer())
                .start(this.itemProcessorStep())
                .build();
    }

    @Bean
    public Step itemProcessorStep() {
        return this.stepBuilderFactory.get("itemProcessorStep")
                .<Person, Person>chunk(10)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .build();
    }

    private ItemReader<? extends Person> itemReader() {
        return new CustomItemReader<>(idItem(10));
    }

    private ItemProcessor<? super Person, ? extends Person> itemProcessor() {
        return item -> {
            if (item.getId() % 2 == 0) {
                return item;
            }
            return null;
        };
    }

    private ItemWriter<? super Person> itemWriter() {
        return items -> items.forEach(item -> log.info("PERSON.ID : {}", item.getId()));
    }

}
