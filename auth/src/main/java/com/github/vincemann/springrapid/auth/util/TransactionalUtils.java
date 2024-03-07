package com.github.vincemann.springrapid.auth.util;


import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class TransactionalUtils {

    /**
     * A convenient method for running code
     * after successful database commit.
     *
     * @param runnable
     */
    public static void afterCommit(Runnable runnable) {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {

            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronizationAdapter() {
                        @Override
                        public void afterCommit() {

                            runnable.run();
                        }
                    });
        }else {
      		runnable.run();
		}
    }


//    /**
//     * Throws a VersionException if the versions of the
//     * given entities aren't same.
//     *
//     * @param original
//     * @param updated
//     */
//    public static <ID extends Serializable>
//    void ensureCorrectVersion(LemonEntity<ID> original, LemonEntity<ID> updated) {
//
//        if (original.getVersion() != updated.getVersion()) {
//            log.warn("invalid version: " + original.getVersion() + " != " + updated.getVersion());
//            throw new VersionException(original.getClass().getSimpleName(), original.getId().toString());
//        }
//    }
}
