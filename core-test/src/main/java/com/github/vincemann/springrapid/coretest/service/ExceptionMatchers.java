package com.github.vincemann.springrapid.coretest.service;

import com.github.vincemann.springrapid.coretest.service.result.matcher.ServiceResultMatcher;
import org.junit.jupiter.api.Assertions;

public class ExceptionMatchers {

    public static ServiceResultMatcher exception(Class<? extends Exception> e){
        return () -> {
            Exception raisedException = ServiceTestTemplate.getInstance().getContext().getServiceResult().getRaisedException();
            if(raisedException==null){
                throw new AssertionError("No Exception thrown");
            }
            if(!raisedException.getClass().equals(e)){
                raisedException.printStackTrace();
                throw new AssertionError("Wrong exception type thrown: "  + raisedException);
            }
        };
    }

    public static ServiceResultMatcher exceptionCauseIs(Class<? extends Exception> e){
        return () -> {
            Exception raisedException = ServiceTestTemplate.getInstance().getContext().getServiceResult().getRaisedException();
            if(raisedException==null){
                throw new AssertionError("No Exception thrown");
            }
            Assertions.assertEquals(e,raisedException.getCause().getClass());
        };
    }

    public static ServiceResultMatcher noException(){
        return () -> {
            Exception raisedException = ServiceTestTemplate.getInstance().getContext().getServiceResult().getRaisedException();
            if(raisedException!=null){
                throw new AssertionError("Exception was thrown: ",raisedException);
            }
        };
    }

}