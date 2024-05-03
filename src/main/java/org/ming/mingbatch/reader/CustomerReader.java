package org.ming.mingbatch.reader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ming.mingbatch.domain.Customer;
import org.ming.mingbatch.domain.Transaction;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.file.FlatFileItemReader;

import java.util.Arrays;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
public class CustomerReader implements ItemStreamReader<Customer> {
    /**
     * 회원 레코드 prefix
     */
    private static final String CUSTOMER_PREFIX = "CUST";
    /**
     * 트랜잭션 레코드 prefix
     */
    private static final String TRANSACTION_PREFIX = "TRANS";
    /**
     * 위임할 플랫 파일 리더
     */
    private final FlatFileItemReader<String> flatFileItemReader;
    /**
     * 파일에서 읽어온 현재 레코드
     */
    private Object curItem = null;

    /**
     * Customer 또는 Transaction 객체를 반환한다.
     *
     * @return 회원 또는 트랜잭션 객체
     */
    private Object readNextItem() throws Exception {

        String nextItem = flatFileItemReader.read();

        if (nextItem == null) {
            log.info("Reached the end of the file.");
            return null;
        }

        List<String> list = Arrays.asList(nextItem.split(","));

        String prefix = list.get(0); // "CUST" or "TRANS"

        if (!prefix.startsWith(CUSTOMER_PREFIX) &&
            !prefix.startsWith(TRANSACTION_PREFIX)) {
            throw new IllegalArgumentException("해당하는 레코드 타입을 찾을 수 없습니다.");
        }

        if (prefix.startsWith(CUSTOMER_PREFIX)) {
            curItem = Customer.from(list);
        }
        if (prefix.startsWith(TRANSACTION_PREFIX)) {
            curItem = Transaction.from(list);
        }

        return curItem;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        flatFileItemReader.open(executionContext);
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        flatFileItemReader.update(executionContext);
    }

    @Override
    public void close() throws ItemStreamException {
        flatFileItemReader.close();
    }

    @Override
    public Customer read() throws Exception {
        if (curItem == null) {
            if (this.readNextItem() instanceof Customer customer) {
                getTransaction(customer);
                return customer;
            }
        }

        if (curItem instanceof Customer customer) {
            getTransaction(customer);
            return customer;
        }

        return null;
    }

    private void getTransaction(Customer customer) throws Exception {
        while (this.readNextItem() instanceof Transaction transaction) {
            customer.addTransaction(transaction);
        }
    }
}
