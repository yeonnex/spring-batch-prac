package org.ming.mingbatch.job.hibernate;

import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import org.ming.mingbatch.job.hibernate.dto.Customer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.HibernateCursorItemReader;
import org.springframework.batch.item.database.builder.HibernateCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.HibernatePagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class HibernateCustomerJobConfig {

    private final static String JOB_NAME = "hibernateCustomerJob";
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
                .reader(itemReader(null, null))
                .writer(itemWriter())
                .build();
    }

    @Bean(JOB_NAME + "_reader")
    @StepScope
    HibernateCursorItemReader<Customer> itemReader(@Value("#{jobParameters['city']}") String city,
                                                   EntityManagerFactory entityManagerFactory) {
        return new HibernateCursorItemReaderBuilder<Customer>()
                .name("customerItemReader")
                .sessionFactory(entityManagerFactory.unwrap(SessionFactory.class))
                .queryString("from Customer where city = :city")
                .parameterValues(Collections.singletonMap("city", city))
                .build();
    }

    @Bean(JOB_NAME + "_writer")
    ItemWriter<Customer> itemWriter() {
        return items -> {
            items.forEach(System.out::println);
        };
    }
}
