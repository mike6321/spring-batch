package com.choi.springbatch.part3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class ItemWriterConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job itemWriterJob() throws Exception {
        return this.jobBuilderFactory.get("itemWriterJob")
                .incrementer(new RunIdIncrementer())
                .start(this.csvItemWriterStep())
                .build();
    }

    @Bean
    public Step csvItemWriterStep() throws Exception {
        return this.stepBuilderFactory.get("csvItemWriterStep")
                .<Person, Person>chunk(10)
                .reader(itemReader())
                .writer(csvFileItemWriter())
                .build();
    }

    private ItemWriter<Person> csvFileItemWriter() throws Exception {
        BeanWrapperFieldExtractor<Person> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"id", "name", "age", "address"});
        DelimitedLineAggregator<Person> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(",");
        lineAggregator.setFieldExtractor(fieldExtractor);

        FlatFileItemWriter<Person> itemWriter = new FlatFileItemWriterBuilder<Person>()
                .name("csvFileItemWriter")
                .encoding("UTF-8")
                .resource(new FileSystemResource("output/test-output.csv"))
                .lineAggregator(lineAggregator)
                .headerCallback(writer -> writer.write("id,이름,나이,거주지"))
                .footerCallback(writer -> writer.write("****************\n"))
                .append(true)
                .build();
        itemWriter.afterPropertiesSet();
        return itemWriter;
    }

    private ItemReader<Person> itemReader() {
        return new CustomItemReader<>(getItems());
    }

    private List<Person> getItems() {
        return IntStream.range(0, 100)
                .mapToObj(index -> new Person(index + 1, "test name" + index, "test age", "test address"))
                .collect(Collectors.toList());
    }

}
