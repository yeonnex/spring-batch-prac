package org.ming.mingbatch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

@Slf4j
public class JobLoggerListener implements JobExecutionListener {
    private static String START_MESSAGE = "{} is beginning execution";
    private static String END_MESSAGE = "{} has completed with the status {}";

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info(START_MESSAGE, jobExecution.getJobInstance().getJobName());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info(END_MESSAGE, jobExecution.getJobInstance().getJobName(), jobExecution.getStatus());
    }
}
