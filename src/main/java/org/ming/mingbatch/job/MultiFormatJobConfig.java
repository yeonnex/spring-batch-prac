package org.ming.mingbatch.job;

import lombok.RequiredArgsConstructor;
import org.ming.mingbatch.domain.Customer;
import org.ming.mingbatch.fieldsetmapper.TransactionFieldSetMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.mapping.PatternMatchingCompositeLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.util.HashMap;

@Configuration
@RequiredArgsConstructor
public class MultiFormatJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    Job multiFormatJob() {
        return this.jobBuilderFactory.get("multiFormat")
                .start(step1())
                .build();
    }

    @Bean
    Step step1() {
        return this.stepBuilderFactory.get("step1")
                .<Customer, Customer>chunk(100)
                .reader(customerItemReader(null))
                .writer(itemWriter())
                .build();
    }

    @Bean
    @StepScope
    FlatFileItemReader<Customer> customerItemReader(@Value("#{jobParameters['customerFile']}") Resource customerFile) {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("customerItemReader")
                .lineMapper(lineMapper())
                .resource(customerFile)
                .build();
    }

    @Bean
    PatternMatchingCompositeLineMapper lineMapper() {
        // 라인 토크나이저
        HashMap<String, LineTokenizer> lineTokenizers = new HashMap<>(2);
        lineTokenizers.put("CUST*", customerLineTokenizer());
        lineTokenizers.put("TRANS*", transactionLineTokenizer());

        // 필드셋 매퍼
        HashMap<String, FieldSetMapper> fieldSetMappers = new HashMap<>(2);
        // - Customer 용
        BeanWrapperFieldSetMapper<Customer> customerFieldSetMapper = new BeanWrapperFieldSetMapper<>();
        customerFieldSetMapper.setTargetType(Customer.class);
        fieldSetMappers.put("CUST*", customerFieldSetMapper);
        // - Transaction 용
        fieldSetMappers.put("TRANS*", new TransactionFieldSetMapper());

        // 라인 매퍼 설정
        PatternMatchingCompositeLineMapper lineMapper = new PatternMatchingCompositeLineMapper();
        lineMapper.setTokenizers(lineTokenizers);
        lineMapper.setFieldSetMappers(fieldSetMappers);

        return lineMapper;

    }

    @Bean
    DelimitedLineTokenizer customerLineTokenizer() {
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setNames("firstName", "middleInitial", "lastName", "address", "city", "state", "zipCode");
        lineTokenizer.setIncludedFields(1, 2, 3, 4, 5, 6, 7);
        return lineTokenizer;
    }

    @Bean
    DelimitedLineTokenizer transactionLineTokenizer() {
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setNames("prefix", "accountNumber", "transactionDate", "amount");
        return lineTokenizer;
    }

    @Bean
    ItemWriter itemWriter() {
        return items -> {
            items.forEach(System.out::println);
        };
    }
}
