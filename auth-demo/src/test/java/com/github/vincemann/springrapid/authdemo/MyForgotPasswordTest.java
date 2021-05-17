package com.github.vincemann.springrapid.authdemo;

import com.github.vincemann.springrapid.authdemo.adapter.EnableProjectComponentScan;
import com.github.vincemann.springrapid.authtests.ForgotPasswordTest;
import org.junit.jupiter.api.Test;

@EnableProjectComponentScan
public class MyForgotPasswordTest extends ForgotPasswordTest {

    @Test
    @Override
    public void anonCanIssueForgotPassword() throws Exception {
        super.anonCanIssueForgotPassword();
    }
}
