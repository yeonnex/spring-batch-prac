package org.ming.mingbatch.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.ming.mingbatch.dto.Customer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.text.SimpleDateFormat;

@Configuration
@RequiredArgsConstructor
public class CustomerJsonJobConfig {
    private static final String JOB_NAME = "customerJsonReadJob";
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean(JOB_NAME)
    Job job() {
        return jobBuilderFactory.get(JOB_NAME)
                .start(step1())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean(JOB_NAME + "_step1")
    Step step1() {
        return stepBuilderFactory.get(JOB_NAME + "_step1")
                .<Customer, Customer>chunk(5)
                .reader(customerJsonItemReader(null))
                .writer(itemWriter())
                .build();
    }

    @Bean(JOB_NAME + "_itemReader")
    @StepScope
    JsonItemReader<Customer> customerJsonItemReader(@Value("#{jobParameters['customerFile']}") Resource resource) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"));
        JacksonJsonObjectReader<Customer> jacksonJsonObjectReader = new JacksonJsonObjectReader<>(org.ming.mingbatch.dto.Customer.class);
        jacksonJsonObjectReader.setMapper(objectMapper);
        return new JsonItemReaderBuilder<Customer>()
                .name("customerFileReader")
                .jsonObjectReader(jacksonJsonObjectReader)
                .resource(resource)
                .build();
    }

    @Bean(JOB_NAME + "_itemWriter")
    ItemWriter<Customer> itemWriter() {
        return items -> {
            items.forEach(System.out::println);
        };
    }
}
