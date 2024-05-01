package org.ming.mingbatch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;

@Slf4j
public class LoggingStepStartStopListener {

    @BeforeStep
    void beforeStep(StepExecution stepExecution) {
        log.info("{} has begun!", stepExecution.getStepName());
    }

    @AfterStep
    ExitStatus afterStep(StepExecution stepExecution) {
        log.info("{} has ended!", stepExecution.getStepName());
        return stepExecution.getExitStatus();
    }
}
