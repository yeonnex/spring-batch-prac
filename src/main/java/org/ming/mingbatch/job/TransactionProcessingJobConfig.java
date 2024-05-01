package org.ming.mingbatch.job;

import lombok.RequiredArgsConstructor;
import org.ming.mingbatch.dao.TransactionDao;
import org.ming.mingbatch.dao.TransactionDaoSupport;
import org.ming.mingbatch.domain.AccountSummary;
import org.ming.mingbatch.domain.Transaction;
import org.ming.mingbatch.processor.TransactionApplierProcessor;
import org.ming.mingbatch.reader.TransactionReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.PassThroughFieldSetMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class TransactionProcessingJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    Job transactionJob() {
        return this.jobBuilderFactory.get("transactionSummaryJob")
                .start(importTransactionFileStep())
                .on("STOPPED").stopAndRestart(importTransactionFileStep())
                .from(importTransactionFileStep()).on("*").to(applyTransactionStep())
                .from(applyTransactionStep()).next(generateAccountSummaryStep())
                .end()
                .build();
    }

    @Bean
    Step importTransactionFileStep() {
        return this.stepBuilderFactory.get("importTransactionFileStep")
                .<Transaction, Transaction>chunk(100)
                .reader(transactionReader())
                .writer(transactionWriter(null))
                .allowStartIfComplete(true)
                .listener(transactionReader())
                .build();
    }

    @Bean
    Step applyTransactionStep() {
        return this.stepBuilderFactory.get("applyTransactionStep")
                .<AccountSummary, AccountSummary>chunk(100)
                .reader(accountSummaryReader(null))
                .processor(transactionApplierProcessor())
                .writer(accountSummaryWriter(null))
                .build();
    }

    @Bean
    Step generateAccountSummaryStep() {
        return this.stepBuilderFactory.get("generateAccountSummaryStep")
                .<AccountSummary, AccountSummary>chunk(100)
                .reader(accountSummaryReader(null))
                .writer(accountSummaryFileWriter(null))
                .build();
    }

    @Bean
    JdbcCursorItemReader<AccountSummary> accountSummaryReader(DataSource dataSource) {
        return new JdbcCursorItemReaderBuilder<AccountSummary>()
                .name("accountSummaryReader")
                .dataSource(dataSource)
                .sql(
                        """
                            SELECT account_number, current_balance FROM account_summary a
                            WHERE a.id IN (SELECT DISTINCT t.account_summary_id FROM transaction t)
                            ORDER BY a.account_number
                            """
                ).rowMapper(((rs, rowNum) -> {
                    AccountSummary accountSummary = new AccountSummary();
                    accountSummary.setAccountNumber(rs.getString("account_number"));
                    accountSummary.setCurrentBalance(rs.getDouble("current_balance"));
                    return accountSummary;
                }))
                .build();
    }

    @Bean
    @StepScope
    FlatFileItemWriter<AccountSummary> accountSummaryFileWriter(@Value("#{jobParameters['summaryFile']}") Resource summaryFile) {
        DelimitedLineAggregator<AccountSummary> lineAggregator = new DelimitedLineAggregator<>();
        BeanWrapperFieldExtractor<AccountSummary> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[] {"accountNumber", "currentBalance"});
        fieldExtractor.afterPropertiesSet();
        lineAggregator.setFieldExtractor(fieldExtractor);
        return new FlatFileItemWriterBuilder<AccountSummary>()
                .name("accountSummaryFileWriter")
                .resource(summaryFile)
                .lineAggregator(lineAggregator)
                .build();
    }

    @Bean
    TransactionApplierProcessor transactionApplierProcessor() {
        return new TransactionApplierProcessor(transactionDao(null));
    }

    @Bean
    JdbcBatchItemWriter<AccountSummary> accountSummaryWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<AccountSummary>()
                .dataSource(dataSource)
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql(
                        """
                            UPDATE account_summary SET current_balance = :currentBalance
                            WHERE account_number = :accountNumber
                            """
                )
                .build();
    }

    @Bean
    @StepScope
    TransactionReader transactionReader() {
        return new TransactionReader(fileItemReader(null));
    }

    @Bean
    @StepScope
    FlatFileItemReader<FieldSet> fileItemReader(@Value("#{jobParameters['transactionFile']}") Resource inputFile) {
        return new FlatFileItemReaderBuilder<FieldSet>()
                .name("fileItemReader")
                .resource(inputFile)
                .lineTokenizer(new DelimitedLineTokenizer())
                .fieldSetMapper(new PassThroughFieldSetMapper())
                .build();
    }

    @Bean
    JdbcBatchItemWriter<Transaction> transactionWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Transaction>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("""
                        INSERT INTO transaction (account_summary_id, timestamp, amount)
                        VALUES ((SELECT id FROM account_summary WHERE account_number = :accountNumber),
                        :timestamp, :amount)
                        """)
                .dataSource(dataSource)
                .build();
    }

    @Bean
    TransactionDao transactionDao(DataSource dataSource) {
        return new TransactionDaoSupport(dataSource);
    }
}
