package org.ming.mingbatch.reader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.DateUtils;
import org.ming.mingbatch.domain.Customer;
import org.ming.mingbatch.domain.Transaction;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.file.FlatFileItemReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CustomerReader implements ItemStreamReader<Customer> {

    private final FlatFileItemReader<String> flatFileItemReader;
    private String  curItem = null;

    @Override
    public Customer read() throws Exception {
        Customer customer = new Customer();
        // "CUST" 이면 "TRANS" 가 끝날떄 까지 계속 읽는다. 다시 "CUST" 가 나오면 해당 읽기를 멈추고 리턴 한다.
        String read = curItem != null ? curItem : flatFileItemReader.read();
        if (read == null) {
            return null;
        }
        if (read.startsWith("CUST")) {
            // 회원 정보 매핑
            List<String> list = Arrays.stream(read.split(",")).toList();

            customer.setFirstName(list.get(1));
            customer.setMiddleInitial(list.get(2));
            customer.setLastName(list.get(3));
            customer.setAddress(list.get(4));
            customer.setCity(list.get(5));
            customer.setState(list.get(6));
            List<Transaction> transactions = new ArrayList<>();

            curItem = flatFileItemReader.read();

            if (curItem == null) {
                return null;
            }

            while ( curItem != null && !curItem.startsWith("CUST") ) {
                list = Arrays.stream(curItem.split(",")).toList();
                Transaction transaction = new Transaction();
                transaction.setAccountNumber(list.get(1));
                String[] patterns = { "yyyy-MM-dd HH:mm:ss" };
                transaction.setTransactionDate(DateUtils.parseDate(list.get(2), patterns));
                transaction.setAmount(Double.parseDouble(list.get(3)));
                transactions.add(transaction);
                curItem = flatFileItemReader.read();
            }
            customer.setTransactions(transactions);
        }

        return customer;
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
}
