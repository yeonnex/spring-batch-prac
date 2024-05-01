package org.ming.mingbatch.job;

import lombok.RequiredArgsConstructor;
import org.ming.mingbatch.domain.Customer;
import org.ming.mingbatch.fieldsetmapper.CustomerFieldSetMapper;
import org.ming.mingbatch.reader.CustomerReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.PassThroughFieldSetMapper;
import org.springframework.batch.item.file.mapping.PassThroughLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
@RequiredArgsConstructor
public class CustomerJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private static final String CUSTOMER_JOB = "customerJob";

    @Bean
    Job customerJob() {
        return this.jobBuilderFactory.get(CUSTOMER_JOB)
                .start(step1())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean(name = CUSTOMER_JOB + "step1")
    Step step1() {
        return this.stepBuilderFactory.get("step1")
                .<Customer, Customer>chunk(3)
                .reader(customerReader(null))
                .writer(customerWriter())
                .build();
    }

    @Bean
    @StepScope
    CustomerReader customerReader(@Value("#{jobParameters['customerFile']}") Resource resource) {

        FlatFileItemReader<String> flatFileItemReader = new FlatFileItemReaderBuilder<String>()
                .name("customerReader")
                .lineMapper(new PassThroughLineMapper())
                .resource(resource)
                .build();

        return new CustomerReader(flatFileItemReader);

    }

    @Bean
    ItemWriter<Customer> customerWriter() {
        return items -> {
            items.forEach(System.out::println);
        };
    }
}
