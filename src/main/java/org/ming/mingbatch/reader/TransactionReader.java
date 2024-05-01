package org.ming.mingbatch.reader;

import org.ming.mingbatch.domain.Transaction;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.transform.FieldSet;

public class TransactionReader implements ItemStreamReader<Transaction> {
    private ItemStreamReader<FieldSet> fieldSetReader;
    private int recordCount = 0;
    private int expectedRecordCount = 0;

    public TransactionReader(ItemStreamReader<FieldSet> fieldSetReader) {
        this.fieldSetReader = fieldSetReader;
    }

    @Override
    public Transaction read() throws Exception {
        return process(fieldSetReader.read());
    }

    private Transaction process(FieldSet fieldSet) {
        Transaction result = null;
        if (fieldSet.getFieldCount() > 1) {
            result = new Transaction();
            result.setAccountNumber(fieldSet.readString(0));
//            result.setTimestamp(fieldSet.readDate(1, "yyyy-MM-DD HH:mm:ss"));
            result.setAmount(fieldSet.readDouble(2));
            recordCount++;
        } else  {
            expectedRecordCount = fieldSet.readInt(0);
        }
        return result;
    }

    @AfterStep
    ExitStatus afterStep(StepExecution stepExecution) {
        if (recordCount == expectedRecordCount) {
            return stepExecution.getExitStatus();
        } else {
            return ExitStatus.STOPPED;
        }
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        this.fieldSetReader.open(executionContext);
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        this.fieldSetReader.update(executionContext);
    }

    @Override
    public void close() throws ItemStreamException {
        this.fieldSetReader.close();
    }
}
