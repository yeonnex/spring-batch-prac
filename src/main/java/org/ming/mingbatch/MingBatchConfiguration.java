package org.ming.mingbatch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ming.mingbatch.incrementer.DailyJobTimeStamper;
import org.ming.mingbatch.listener.JobLoggerListener;
import org.ming.mingbatch.validator.ParameterValidator;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@EnableBatchProcessing
@Configuration
@RequiredArgsConstructor
public class MingBatchConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    JobParametersValidator validator() {
        return new ParameterValidator();
    }

    @Bean
    Job mingJob() {
        return jobBuilderFactory.get("mingJob")
                .validator(validator())
                .incrementer(new DailyJobTimeStamper())
                .listener(new JobLoggerListener())
                .start(step1())
                .build();
    }

    @Bean
    Step step1() {
        return stepBuilderFactory.get("step1")
                .tasklet((contribution, chunkContext) -> {
                    String name = (String) chunkContext.getStepContext().getJobParameters().get("name");
                    log.info("Hello, {}", name);
                    for (int i = 0; i < 10; i++) {
                        System.out.println("ming ming batch ... " + i);
                        Thread.sleep(1000);
                    }
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    StepExecutionListener promotionListener() {
        ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
        listener.setKeys(new String[] {"name"});
        return listener;
    }
}
