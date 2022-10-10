package com.choi.springbatch.part5;

import io.micrometer.core.instrument.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

import static org.springframework.batch.core.job.flow.FlowExecutionStatus.COMPLETED;

@RequiredArgsConstructor
public class JobParametersDecide implements JobExecutionDecider {

    public static FlowExecutionStatus CONTINUE = new FlowExecutionStatus("CONTINUE");
    private final String key;

    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
        String value = jobExecution.getJobParameters().getString(key);

        if (StringUtils.isEmpty(value)) {
            return COMPLETED;
        }

        return CONTINUE;
    }

}
