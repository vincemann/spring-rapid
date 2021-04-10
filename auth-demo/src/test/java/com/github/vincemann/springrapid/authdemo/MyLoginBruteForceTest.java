package com.github.vincemann.springrapid.authdemo;

import com.github.vincemann.springrapid.authdemo.adapter.EnableProjectComponentScan;
import com.github.vincemann.springrapid.authtests.LoginBruteForceTest;
import org.junit.jupiter.api.Test;

@EnableProjectComponentScan
public class MyLoginBruteForceTest extends LoginBruteForceTest {

    @Test
    @Override
    public void tooManyLoginTries_tooManyRequestsResponse() throws Exception {
        super.tooManyLoginTries_tooManyRequestsResponse();
    }

    @Test
    @Override
    public void maxLoginTries_thenCorrectLogin_resetsEverything() throws Exception {
        super.maxLoginTries_thenCorrectLogin_resetsEverything();
    }
}
