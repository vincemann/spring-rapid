package com.github.vincemann.springrapid.authdemo;

import com.github.vincemann.springrapid.authdemo.adapter.EnableProjectComponentScan;
import com.github.vincemann.springrapid.authtests.LoginBruteForceTest;
import org.junit.jupiter.api.Test;

@EnableProjectComponentScan
public class MyLoginBruteForceTest extends LoginBruteForceTest {

    @Test
    @Override
    public void tooManyLoginTries() throws Exception {
        super.tooManyLoginTries();
    }

    @Test
    @Override
    public void almostTooManyLoginTries_thenRightLogin_shouldReset() throws Exception {
        super.almostTooManyLoginTries_thenRightLogin_shouldReset();
    }
}
