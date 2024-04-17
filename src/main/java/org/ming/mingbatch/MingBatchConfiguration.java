package org.ming.mingbatch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ming.mingbatch.incrementer.DailyJobTimeStamper;
import org.ming.mingbatch.listener.JobLoggerListener;
import org.ming.mingbatch.service.CustomService;
import org.ming.mingbatch.validator.ParameterValidator;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.core.step.tasklet.MethodInvokingTaskletAdapter;
import org.springframework.beans.factory.annotation.Value;
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
                .tasklet(tasklet(null)).build();
    }

    @Bean
    StepExecutionListener promotionListener() {
        ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
        listener.setKeys(new String[]{"name"});
        return listener;
    }

    @Bean
    @StepScope
    MethodInvokingTaskletAdapter tasklet(@Value("#{jobParameters['name']}") String name) {
        MethodInvokingTaskletAdapter methodInvokingTaskletAdapter = new MethodInvokingTaskletAdapter();
        methodInvokingTaskletAdapter.setTargetObject(customService());
        methodInvokingTaskletAdapter.setTargetMethod("hello");
        methodInvokingTaskletAdapter.setArguments(new String[]{name});
        return methodInvokingTaskletAdapter;
    }

    @Bean
    CustomService customService() {
        return new CustomService();
    }
}
