package org.ming.mingbatch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class MingBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(MingBatchApplication.class, args);
    }

}
