package org.ming.mingbatch.job.jdbc;

import lombok.RequiredArgsConstructor;
import org.ming.mingbatch.job.jdbc.dto.Customer;
import org.ming.mingbatch.job.jdbc.rowmapper.CustomerRowMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;

import javax.sql.DataSource;

/**
 * 잡 파라미터로 city 에 대한 값을 준 뒤 잡을 실행시키세요.
 * ex) java -jar myJob.jar city=Chicago
 */
@Configuration
@RequiredArgsConstructor
public class JdbcCursorCustomerJobConfig {

    private final static String JOB_NAME = "JdbcCursorCustomerJob";
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean(JOB_NAME)
    Job job() {
        return jobBuilderFactory.get(JOB_NAME)
                .start(step1())
                .build();
    }

    @Bean(JOB_NAME + "_step1")
    Step step1() {
        return stepBuilderFactory.get(JOB_NAME + "_step1")
                .<Customer, Customer>chunk(10)
                .reader(itemReader(null))
                .writer(itemWriter())
                .build();
    }

    @Bean(JOB_NAME + "_itemReader")
    JdbcCursorItemReader<Customer> itemReader(DataSource dataSource) {
        return new JdbcCursorItemReaderBuilder<Customer>()
                .name("cursorItemReader")
                .dataSource(dataSource)
                .sql("SELECT * FROM CUSTOMER WHERE city = ?")
                .rowMapper(new CustomerRowMapper())
                .preparedStatementSetter(citySetter(null))
                .build();
    }

    @Bean
    @StepScope
    ArgumentPreparedStatementSetter citySetter(@Value("#{jobParameters['city']}") String city) {
        return new ArgumentPreparedStatementSetter(new Object[] {city});
    }

    @Bean(JOB_NAME + "_itemWriter")
    ItemWriter<Customer> itemWriter() {
        return items -> {
            System.out.println("size: " + items.size());
            items.forEach(System.out::println);
        };
    }

}

