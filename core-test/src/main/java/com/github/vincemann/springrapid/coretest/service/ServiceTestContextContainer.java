package com.github.vincemann.springrapid.coretest.service;

import com.github.vincemann.springrapid.coretest.service.result.ServiceTestContext;
import org.junit.jupiter.api.TestInfo;

import java.util.HashMap;
import java.util.Map;

public class ServiceTestContextContainer {

    private static Map<TestInfo, ServiceTestContext> testContextMap = new HashMap<>();

    static ServiceTestContext findTestContext(TestInfo testInfo){
        ServiceTestContext testContext = testContextMap.get(testInfo);
        if (testContext==null){
            throw new IllegalStateException("TestContext not initialized");
        }
        return testContext;
    }

    static void register(TestInfo testInfo, ServiceTestContext context){
        ServiceTestContext old = testContextMap.get(testInfo);
        if (old!=null){
            throw new IllegalStateException("New Context can only be created after old is removed");
        }
        testContextMap.put(testInfo,context);
    }

    static void remove(TestInfo testInfo){
        testContextMap.remove(testInfo);
    }
}
