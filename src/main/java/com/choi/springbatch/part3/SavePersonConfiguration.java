package com.choi.springbatch.part3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.support.builder.CompositeItemProcessorBuilder;
import org.springframework.batch.item.support.builder.CompositeItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.persistence.EntityManagerFactory;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class SavePersonConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job savePersonJob() throws Exception {
        return this.jobBuilderFactory.get("savePersonJob")
                .incrementer(new RunIdIncrementer())
                .start(this.savePersonStep(null))
                .listener(new SavePersonListener.SavePersonJobExecutionListener())
                .listener(new SavePersonListener.SavePersonAnnotationJobExecutionListener())
                .build();
    }

    @Bean
    @JobScope
    public Step savePersonStep(@Value("#{jobParameters[allow_duplicate]}") String allowDuplicate) throws Exception {
        return this.stepBuilderFactory.get("savePersonStep")
                .<Person, Person>chunk(10)
                .reader(itemReader())
//                .processor(new DuplicateValidationProcessor<>(Person::getName, Boolean.parseBoolean(allowDuplicate)))
                .processor(itemProcessor(allowDuplicate))
                .writer(itemWriter())
                .listener(new SavePersonListener.SavePersonAnnotationStepExecutionListener())
                .faultTolerant()
                .skip(NotFoundNameException.class)
                .skipLimit(3)
                .build();
    }

    private ItemProcessor<? super Person, ? extends Person> itemProcessor(String allowDuplicate) throws Exception {
        // name 기준으로 중복제거
        // -> 이로인하여 UNKNOWN 변경 시 4개의 row 생성 된다.
        DuplicateValidationProcessor<Person> duplicateValidationProcessor = new DuplicateValidationProcessor<>(Person::getName, Boolean.parseBoolean(allowDuplicate));
        ItemProcessor<Person, Person> validationProcessor = item -> {
            if (item.isNotEmptyName()) {
                return item;
            }
            throw new NotFoundNameException();
        };

        CompositeItemProcessor<Person, Person> itemProcessor = new CompositeItemProcessorBuilder<Person, Person>()
                .delegates(new PersonValidationRetryProcessor(), validationProcessor, duplicateValidationProcessor)
                .build();
        itemProcessor.afterPropertiesSet();
        return itemProcessor;
    }

    private ItemReader<? extends Person> itemReader() throws Exception {
        DefaultLineMapper<Person> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames("name", "age", "address");
        lineMapper.setLineTokenizer(tokenizer);

        lineMapper.setFieldSetMapper(fieldSet -> new Person(
            fieldSet.readString(0),
            fieldSet.readString(1),
            fieldSet.readString(2)
        ));

        FlatFileItemReader<Person> savePersonItemReader = new FlatFileItemReaderBuilder<Person>()
                .name("savePersonItemReader")
                .encoding("UTF-8")
                .resource(new ClassPathResource("person.csv"))
                .linesToSkip(1)
                .lineMapper(lineMapper)
                .build();
        savePersonItemReader.afterPropertiesSet();

        return savePersonItemReader;
    }

    private ItemWriter<? super Person> itemWriter() throws Exception {
        JpaItemWriter<Person> jpaItemWriter = new JpaItemWriterBuilder<Person>()
                .entityManagerFactory(entityManagerFactory)
                .build();
        ItemWriter<Person> logItemWrite = items -> log.info("person.size :: {}", items.size());

        CompositeItemWriter<Person> itemWriter = new CompositeItemWriterBuilder<Person>()
                .delegates(jpaItemWriter, logItemWrite)
                .build();
        itemWriter.afterPropertiesSet();
        return itemWriter;
    }

}
