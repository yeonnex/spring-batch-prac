package org.ming.mingbatch.incrementer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersIncrementer;

import java.util.Date;

@Slf4j
public class DailyJobTimeStamper implements JobParametersIncrementer {
    @Override
    public JobParameters getNext(JobParameters jobParameters) {
        Date currentDate = new Date();
        log.info(String.valueOf(currentDate));
        return new JobParametersBuilder(jobParameters)
                .addDate("currentDate", currentDate)
                .toJobParameters();
    }
}
