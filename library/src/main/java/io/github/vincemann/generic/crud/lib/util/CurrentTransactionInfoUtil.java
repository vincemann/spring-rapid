package io.github.vincemann.generic.crud.lib.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
public class CurrentTransactionInfoUtil {

    public static void printInfo(){
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            try {
                TransactionStatus status = TransactionAspectSupport.currentTransactionStatus();
                log.debug("curr transaction is active: " + status);
            }catch (NoTransactionException e){
                log.debug("no transaction active");
            }

        }
    }
}
