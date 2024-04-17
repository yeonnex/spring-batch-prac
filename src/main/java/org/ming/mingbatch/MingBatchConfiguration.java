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
import org.springframework.batch.core.step.tasklet.SystemCommandTasklet;
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
                .tasklet(tasklet()).build();
    }

    @Bean
    StepExecutionListener promotionListener() {
        ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
        listener.setKeys(new String[]{"name"});
        return listener;
    }

    @Bean
    SystemCommandTasklet tasklet() {
        SystemCommandTasklet systemCommandTasklet = new SystemCommandTasklet();
        systemCommandTasklet.setCommand("mkdir ming");
        systemCommandTasklet.setTimeout(5000);
        systemCommandTasklet.setInterruptOnCancel(true);
        return systemCommandTasklet;
    }
}
