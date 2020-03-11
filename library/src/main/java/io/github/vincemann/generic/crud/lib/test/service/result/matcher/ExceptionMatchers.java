package io.github.vincemann.generic.crud.lib.test.service.result.matcher;

import org.junit.jupiter.api.Assertions;

public class ExceptionMatchers {

    public static ServiceResultMatcher exception(Class<? extends Exception> e){
        return (serviceResult, context, repository) -> {
            Exception raisedException = serviceResult.getRaisedException();
            if(raisedException==null){
                throw new AssertionError("No Exception thrown");
            }
            if(!raisedException.getClass().equals(e)){
                throw new AssertionError("Wrong exception type thrown: "  + raisedException.getClass());
            }
        };
    }

    public static ServiceResultMatcher exceptionCauseIs(Class<? extends Exception> e){
        return (serviceResult, context, repository) -> {
            Exception raisedException = serviceResult.getRaisedException();
            if(raisedException==null){
                throw new AssertionError("No Exception thrown");
            }
            Assertions.assertEquals(e,raisedException.getCause().getClass());
        };
    }

    public static ServiceResultMatcher noException(){
        return (serviceResult, context, repository) -> {
            Exception raisedException = serviceResult.getRaisedException();
            if(raisedException!=null){
                throw new AssertionError("No Exception thrown");
            }
        };
    }

}
