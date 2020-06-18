/***********************************************************************************
 * Copyright (c) 2013. Nickolay Gerilovich. Russia.
 *   Some Rights Reserved.
 ************************************************************************************/

package com.github.vincemann.springrapid.log.nickvl.service;

import com.github.vincemann.springrapid.log.nickvl.annotation.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;

import static com.github.vincemann.springrapid.log.nickvl.annotation.LogException.Exc;

/**
 * Simple service, implementation of {@link FooService}.
 */
@Service
public class SimpleFooService implements FooService {

    @Log(logPoint = LogPoint.IN)
    @LogTrace
    @Override
    public void voidMethodZero() {
        // nothing to do
    }

    @Override
    public String stringMethodOne(String first) {
        return "stringMethodOne:" + first;
    }

    @LogDebug
    @Override
    public String stringMethodTwo(String first, @Lp String second) {
        return "stringMethodTwo:" + first + ":" + second;
    }

    @LogDebug
    @Override
    public String stringMethodThree(String first, String second, String third) {
        return "stringMethodThree:" + first + ":" + second + ":" + third;
    }

    @LogTrace
    @LogException
    @Override
    public String stringMethodTwoVarargs(String first, @Lp String... second) {
        return "stringMethodTwoVarargs:" + first + ":" + Arrays.toString(second);
    }

    @LogDebug
    @LogException(value = {@Exc(value = Exception.class, stacktrace = true)}, warn = {@Exc({IllegalArgumentException.class, IOException.class})})
    @Override
    public void voidExcMethodZero() throws IOException {
        throw new IOException("io fail");
    }
}
