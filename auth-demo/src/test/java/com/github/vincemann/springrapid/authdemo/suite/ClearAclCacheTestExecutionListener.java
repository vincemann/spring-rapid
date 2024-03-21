package com.github.vincemann.springrapid.authdemo.suite;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.AclCache;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

public class ClearAclCacheTestExecutionListener extends AbstractTestExecutionListener {


    @Autowired
    private AclCache aclCache;


    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        super.afterTestMethod(testContext);
        testContext.getApplicationContext()
                .getAutowireCapableBeanFactory()
                .autowireBean(this);
        try{
            aclCache.clearCache();
        }catch (IllegalStateException e){
            System.err.println("Could not clear cache: " + e);
        }

    }
}
