package io.github.vincemann.springrapid.core.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.InvocationTargetException;

/**
 * Transaction Logging support.
 */
@Slf4j
public class DebugTransactionUtil {

//    public static void printInfo(){
//        if (TransactionSynchronizationManager.isActualTransactionActive()) {
//            try {
//                TransactionStatus status = TransactionAspectSupport.currentTransactionStatus();
//                log.debug("curr transaction is active: " + status);
//            }catch (NoTransactionException e){
//                log.debug("no transaction active");
//            }
//
//        }
//    }

    private static final boolean transactionDebugging = true;
    private static final boolean verboseTransactionDebugging = true;

    public static void showTransactionStatus(Class clazz, String method) {
        String message = clazz.getSimpleName()+"."+method;
        log.debug(((transactionActive()) ? "[+] " : "[-] ") + message);
        try{
            TransactionStatus status = TransactionAspectSupport.currentTransactionStatus();
            log.debug("Status: "+status);
        }catch (NoTransactionException e){
            log.debug("No Status could be found");
        }
    }


    // Some guidance from: http://java.dzone.com/articles/monitoring-declarative-transac?page=0,1
    public static boolean transactionActive() {
        try {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            Class tsmClass = contextClassLoader.loadClass("org.springframework.transaction.support.TransactionSynchronizationManager");
            Boolean isActive = (Boolean) tsmClass.getMethod("isActualTransactionActive", null).invoke(null, null);

            return isActive;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        // If we got here it means there was an exception
        throw new IllegalStateException("ServerUtils.transactionActive was unable to complete properly");
    }

    public static void transactionRequired(Class clazz, String method) {
        // Are we debugging transactions?
        if (!transactionDebugging) {
            // No, just return
            return;
        }

        String message = clazz.getSimpleName()+"."+method;
        // Are we doing verbose transaction debugging?
        if (verboseTransactionDebugging) {
            // Yes, show the status before we get to the possibility of throwing an exception
            showTransactionStatus(clazz,method);
        }

        // Is there a transaction active?
        if (!transactionActive()) {
            // No, throw an exception
            throw new IllegalStateException("Transaction required but not active [" + message + "]");
        }
    }

}
