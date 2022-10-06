package com.choi.springbatch.part4;

import com.choi.springbatch.TestConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = {
        UserConfiguration.class,
        TestConfiguration.class
})
@ExtendWith({SpringExtension.class})
@SpringBatchTest
class UserConfigurationTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void test() throws Exception {
        // given
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        // when
        int size =  userRepository.findAllByUpdatedDate(LocalDate.now()).size();
        // then
        assertThat(
                jobExecution.getStepExecutions()
                .stream()
                .filter(x -> x.getStepName().equals("userLevelUpStep"))
                .mapToInt(StepExecution::getWriteCount)
                .sum()
        ).isEqualTo(size)
                .isEqualTo(300);
        assertThat(userRepository.count())
                .isEqualTo(400);
    }

}
