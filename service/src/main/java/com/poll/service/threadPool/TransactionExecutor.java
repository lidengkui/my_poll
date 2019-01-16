package com.poll.service.threadPool;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

public class TransactionExecutor {

    private ThreadPoolTaskExecutor taskExecutor;

    private TransactionTemplate transactionTemplate;

    public TransactionExecutor(
            ThreadPoolTaskExecutor taskExecutor, TransactionTemplate transactionTemplate) {
        this.taskExecutor = taskExecutor;
        this.transactionTemplate = transactionTemplate;
    }

    public void run(final Runnable runnable) {
        taskExecutor.execute(
                () ->
                        transactionTemplate.execute(
                                new TransactionCallbackWithoutResult() {
                                    @Override
                                    protected void doInTransactionWithoutResult(TransactionStatus status) {
                                        runnable.run();
                                    }
                                }));
    }
}
