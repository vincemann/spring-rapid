package com.github.vincemann.springrapid.coretest.util;

import org.springframework.test.util.AopTestUtils;

public class SpyBeanUtils {

    /**
     * Use in combination with @{@link org.springframework.boot.test.mock.mockito.SpyBean}.
     * If you get something like : rg.mockito.exceptions.misusing.NotAMockException: Argument should be a mock, but is: class com.blah.MyServiceImpl$$EnhancerBySpringCGLIB$$9712a2a5
     * example:
     *
     *
     * @SpyBean
     * Extension extension;
     *
     * ...
     *
     * doReturn(Boolean.TRUE)
     *                 .when(unproxy(extension)).isInTimeFrame(any(Rating.class));
     */
    public static  <T> T unproxy(T spy){
        //        https://stackoverflow.com/questions/9033874/mocking-a-property-of-a-cglib-proxied-service-not-working
        return AopTestUtils.getUltimateTargetObject(spy);
    }
}
