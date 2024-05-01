package org.ming.mingbatch.job;

import lombok.RequiredArgsConstructor;
import org.ming.mingbatch.domain.Customer;
import org.ming.mingbatch.fieldsetmapper.CustomerFieldSetMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

//@Configuration
@RequiredArgsConstructor
public class CustomerFixedWidthJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;


    @Bean(name = "customerJob")
    Job myCopyJob() {
        return this.jobBuilderFactory.get("copyJob")
                .start(copyStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    Step copyStep() {
        return this.stepBuilderFactory.get("copyStep")
                .<Customer, Customer>chunk(10)
                .reader(customerItemReader(null))
                .writer(itemWriter())
                .build();
    }

    @Bean
    @StepScope
    FlatFileItemReader<Customer> customerItemReader(@Value("#{jobParameters['customerFile']}") Resource inputFile) {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("customerItemReader")
                .resource(inputFile)
                .fieldSetMapper(new CustomerFieldSetMapper())
                .fixedLength()
                .columns(
                        new Range[]{
                                new Range(1, 11), new Range(12, 12), new Range(13, 22), new Range(23, 26),
                                new Range(27, 46), new Range(47, 62), new Range(63, 64), new Range(65, 69)
                        }
                )
                .names("firstName", "middleInitial", "lastName",
        "addressNumber", "street", "city", "state", "zipCode")
//                .targetType(Customer.class)
                .build();
    }

    @Bean
    ItemWriter<Customer> itemWriter() {
        return items -> {
            items.forEach(System.out::println);
        };
    }
}
