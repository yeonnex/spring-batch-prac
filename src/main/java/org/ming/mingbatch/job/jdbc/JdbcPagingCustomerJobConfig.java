package org.ming.mingbatch.job.jdbc;

import lombok.RequiredArgsConstructor;
import org.ming.mingbatch.job.jdbc.dto.Customer;
import org.ming.mingbatch.job.jdbc.rowmapper.CustomerRowMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 잡 파리미터로 city=Chicago 를 설정하고 잡을 실행하세요!
 */
@Configuration
@RequiredArgsConstructor
public class JdbcPagingCustomerJobConfig {
    private static final String JOB_NAME = "jdbcCustomerPagingJob";
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean(JOB_NAME)
    Job job() {
        return this.jobBuilderFactory.get(JOB_NAME)
                .start(step1())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean(JOB_NAME + "_step1")
    Step step1() {
        return this.stepBuilderFactory.get(JOB_NAME + "_step1")
                .<Customer, Customer>chunk(5)
                .reader(itemReader(null, null, null))
                .writer(itemWriter())
                .build();
    }

    /**
     * 잡 파리미터로 city=Chicago 를 설정하고 잡을 실행하세요!
     *
     * @param dataSource
     * @param queryProvider
     * @param city
     * @return
     */
    @Bean(JOB_NAME + "_itemWriter")
    @StepScope
    JdbcPagingItemReader<Customer> itemReader(DataSource dataSource,
                                              PagingQueryProvider queryProvider,
                                              @Value("#{jobParameters['city']}") String city) {
        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("city", city);
        return new JdbcPagingItemReaderBuilder<Customer>()
                .name("itemReader")
                .dataSource(dataSource)
                .queryProvider(queryProvider)
                .parameterValues(parameterValues)
                .pageSize(10)
                .rowMapper(new CustomerRowMapper())
                .build();
    }

    @Bean
    SqlPagingQueryProviderFactoryBean pagingQueryProvider(DataSource dataSource) {
        SqlPagingQueryProviderFactoryBean factoryBean = new SqlPagingQueryProviderFactoryBean();
        factoryBean.setSelectClause("SELECT *");
        factoryBean.setFromClause("FROM customer");
        factoryBean.setWhereClause("WHERE city = :city");
        factoryBean.setSortKey("lastName");
        factoryBean.setDataSource(dataSource);
        return factoryBean;
    }

    @Bean(JOB_NAME + "_writer")
    ItemWriter<Customer> itemWriter() {
        return items -> items.forEach(System.out::println);
    }


}
