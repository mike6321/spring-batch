package com.choi.springbatch.part4;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class LevelUpExecutionListener implements JobExecutionListener {

    private final UserRepository userRepository;

    @Override
    public void beforeJob(JobExecution jobExecution) {

    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        List<User> users = userRepository.findAllByUpdatedDate(LocalDate.now());
        long time = Objects.requireNonNull(jobExecution.getEndTime()).getTime()
                - Objects.requireNonNull(jobExecution.getStartTime()).getTime();
        log.info("회원등급 업데이트 배치 프로그램");
        log.info("****************************");
        log.info("총 데이터 처리 {} 건, 처리시간 {}milis", users.size(), time);
        log.info("****************************");
    }

}
