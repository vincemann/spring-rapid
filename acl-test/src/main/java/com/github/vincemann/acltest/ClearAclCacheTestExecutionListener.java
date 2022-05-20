package com.github.vincemann.acltest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.AclCache;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

@Slf4j
public class ClearAclCacheTestExecutionListener extends AbstractTestExecutionListener {


    //todo das geht bestimmt besser, siehe docs
    //ansonsten in default testAnnotation als TestExecutionListener einbauen
    @Autowired
    private AclCache aclCache;

    @Override
    public void beforeTestClass(TestContext testContext) {
//        testContext.getApplicationContext()
//                .getAutowireCapableBeanFactory()
//                .autowireBean(this);
    }

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        super.afterTestMethod(testContext);
        testContext.getApplicationContext()
                .getAutowireCapableBeanFactory()
                .autowireBean(this);
        try{
            aclCache.clearCache();
        }catch (IllegalStateException e){
            log.warn("Could not clear cache: ", e);
        }

    }
}
