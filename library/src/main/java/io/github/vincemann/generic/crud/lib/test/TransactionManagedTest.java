package io.github.vincemann.generic.crud.lib.test;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * TestClasses implementing this interface to precicsely manage transactions
 */
public interface TransactionManagedTest {
    //TRANSACTION MANAGEMENT & PROVIDE BUNDLES
    /**
     * Starts Transaction that should be committed in the running test that is calling this method
     * @return
     */
    public default TransactionStatus startTestTransaction(){
        DefaultTransactionDefinition testTransactionDefinition = new DefaultTransactionDefinition();
        testTransactionDefinition.setName("testTransaction");
        return getPlatformTransactionManager().getTransaction(testTransactionDefinition);
    }
    public default TransactionStatus provideBundlesAndStartTransaction() throws Exception {
        TransactionStatus testTransaction = startTestTransaction();
        provideBundles();
        return testTransaction;
    }
    public default void provideBundlesAndCommitTransaction() throws Exception {
        TransactionStatus testTransaction = startTestTransaction();
        provideBundles();
        getPlatformTransactionManager().commit(testTransaction);
    }
    //provide entity bundles in memory, but dont apply changes to database
    //this is done because entities saved within "provide entityBundle process" must be persisted within same transaction
    //as persisting actions in tests -> otherwise there will be an exception because detached entities cant be persisted anymore
    /**
     * use if you want the to provide the bundles but dont wish to persist any entities created/saved in provide process to be saved to database
     * @throws Exception
     */
    public default void provideBundlesAndRollbackTransaction() throws Exception {
        TransactionStatus testTransaction = startTestTransaction();
        provideBundles();
        getPlatformTransactionManager().rollback(testTransaction);
    }

    public PlatformTransactionManager getPlatformTransactionManager();

    /**
     * All managed TestBundles should be obtained in this method so they are within the TestTransaction
     * @throws Exception
     */
    public abstract void provideBundles() throws Exception;
}
