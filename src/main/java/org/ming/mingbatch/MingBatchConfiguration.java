package org.ming.mingbatch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ming.mingbatch.chunkPolicy.RandomChunkPolicy;
import org.ming.mingbatch.dto.response.CustomerTransactionCountResponse;
import org.ming.mingbatch.incrementer.DailyJobTimeStamper;
import org.ming.mingbatch.listener.JobLoggerListener;
import org.ming.mingbatch.respository.AccountRepository;
import org.ming.mingbatch.validator.ParameterValidator;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;

/**
 * 월별 트랜잭션 횟수가 10번 이상인 고객 찾기
 */
@Slf4j
@EnableBatchProcessing
@Configuration
@RequiredArgsConstructor
public class MingBatchConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final AccountRepository accountRepository;
    private final DataSource dataSource;

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
                .<CustomerTransactionCountResponse, CustomerTransactionCountResponse>chunk(new RandomChunkPolicy())
                .reader(itemReader())
                .writer(itemWriter())
                .build();
    }

    @Bean
    JdbcCursorItemReader<CustomerTransactionCountResponse> itemReader() {
        return new JdbcCursorItemReaderBuilder<CustomerTransactionCountResponse>()
                .name("jdbcCursorItemReader")
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(CustomerTransactionCountResponse.class))
                .sql(AccountRepository.SELECT_TRANSACTION_COUNT_BY_CUSTOMER_QUERY)
                .build();
    }

    @Bean
    ItemWriter<CustomerTransactionCountResponse> itemWriter() {
        return items -> {
            items.forEach(System.out::println);
        };
    }
}
