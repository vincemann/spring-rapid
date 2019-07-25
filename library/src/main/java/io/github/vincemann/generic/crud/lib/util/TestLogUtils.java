package io.github.vincemann.generic.crud.lib.util;

import org.slf4j.Logger;

import java.util.Map;

public class TestLogUtils {

    private static final String BEFORE_AFTER_PADDING = "-------------------------------------------------------------------------";
    private static final String LEFT_RIGHT_PADDING = "########################";

    public static void logTestStart(Logger logger, String testName, Map.Entry<String,Object>... loggedKeyValuePairs){
        logTest(logger,testName,"starts",loggedKeyValuePairs);
    }

    public static void logTestSucceeded(Logger logger, String testName, Map.Entry<String,Object>... loggedKeyValuePairs){
        logTest(logger,testName,"succeeded",loggedKeyValuePairs);
    }

    public static void logTestFailed(Logger logger, String testName, Map.Entry<String,Object>... loggedKeyValuePairs){
        logTest(logger,testName,"failed",loggedKeyValuePairs);
    }

    private static void logTest(Logger logger, String testName,String testStateIndicator,  Map.Entry<String,Object>... loggedKeyValuePairs){
        logger.info("");
        logger.info("");
        logger.info(BEFORE_AFTER_PADDING);
        logger.info(LEFT_RIGHT_PADDING+" Test: " + testName+" " + testStateIndicator+". "+LEFT_RIGHT_PADDING);
        for (Map.Entry<String, Object> loggedKeyValuePair : loggedKeyValuePairs) {
            logger.info(loggedKeyValuePair.getKey()+" = "+loggedKeyValuePair.getValue());
        }
        logger.info(BEFORE_AFTER_PADDING);
        logger.info("");
        logger.info("");
    }
}
