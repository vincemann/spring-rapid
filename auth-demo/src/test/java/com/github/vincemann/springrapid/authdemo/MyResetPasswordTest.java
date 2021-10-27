package com.github.vincemann.springrapid.authdemo;

import com.github.vincemann.springrapid.authdemo.adapter.EnableProjectComponentScan;
import com.github.vincemann.springrapid.authtests.ResetPasswordTest;
import org.junit.jupiter.api.Test;

@EnableProjectComponentScan
public class MyResetPasswordTest extends ResetPasswordTest {

    @Test
    @Override
    public void getDirectedToForgotPasswordPage() throws Exception {
        super.getDirectedToForgotPasswordPage();
    }
}
