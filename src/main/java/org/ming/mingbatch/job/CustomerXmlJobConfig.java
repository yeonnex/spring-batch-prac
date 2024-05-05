package org.ming.mingbatch.job;

import lombok.RequiredArgsConstructor;
import org.ming.mingbatch.dto.Customer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.oxm.xstream.XStreamMarshaller;

import java.util.HashMap;

@Configuration
@RequiredArgsConstructor
public class CustomerXmlJobConfig {
    private static final String JOB_NAME = "customerXMLReadJob";
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean(JOB_NAME)
    Job job() {
        return jobBuilderFactory.get(JOB_NAME).start(step())
                .incrementer(new RunIdIncrementer()).build();
    }

    @Bean(JOB_NAME + "_step")
    Step step() {
        return stepBuilderFactory.get(JOB_NAME + "_step").<Customer, Customer>chunk(5).reader(customerItemReader(null)).writer(itemWriter()).build();
    }

    @Bean(JOB_NAME + "itemReader")
    @StepScope
    public StaxEventItemReader<Customer> customerItemReader(@Value("#{jobParameters['customerFile']}") Resource resource) {
        return new StaxEventItemReaderBuilder<Customer>()
                .name("xmlItemReader")
                .resource(resource)
                .addFragmentRootElements("customer")
                .unmarshaller(customerMarshaller())
                .build();
    }

    @Bean
    XStreamMarshaller customerMarshaller() {
        HashMap<String, Object> alises = new HashMap<>();
        alises.put("customer", Customer.class);
        alises.put("firstName", String.class);
        XStreamMarshaller marshaller = new XStreamMarshaller();
        marshaller.setAliases(alises);
        return marshaller;
    }

    @Bean(JOB_NAME + "_writer")
    ItemWriter<Customer> itemWriter() {
        return items -> {
            items.forEach(System.out::println);
        };
    }


}
