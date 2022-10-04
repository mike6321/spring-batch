package com.choi.springbatch.part3;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.*;

@ContextConfiguration(classes = {
        SavePersonConfiguration.class,
        TestConfiguration.class
})
@ExtendWith({SpringExtension.class})
@SpringBatchTest
class SavePersonConfigurationTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
    @Autowired
    private PersonRepository personRepository;

    @AfterEach
    void tearDown() {
        personRepository.deleteAll();
    }

    @Test
    public void test_allow_duplicate() throws Exception {
        // given
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("allow_duplicate", "false")
                .toJobParameters();

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        // then
        assertThat(
                jobExecution.getStepExecutions()
                        .stream()
                        .mapToInt(StepExecution::getWriteCount)
                        .sum())
                .isEqualTo(3)
                .isEqualTo(personRepository.count());
    }

    @Test
    public void test_not_allow_duplicate() throws Exception {
        // given
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("allow_duplicate", "true")
                .toJobParameters();

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        // then
        assertThat(jobExecution.getStepExecutions()
                            .stream()
                            .mapToInt(StepExecution::getWriteCount)
                            .sum())
                .isEqualTo(102)
                .isEqualTo(personRepository.count());
    }

    @Test
    void test_step() {
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("savePersonStep");
        assertThat(jobExecution.getStepExecutions()
                        .stream()
                        .mapToInt(StepExecution::getWriteCount)
                        .sum())
                .isEqualTo(3)
                .isEqualTo(personRepository.count());
    }

}
