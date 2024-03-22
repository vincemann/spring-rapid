package com.github.vincemann.springrapid.authdemo;

import com.github.vincemann.springrapid.authtests.tests.ResetPasswordTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;


@Tag(value = "demo-projects")
public class MyResetPasswordTest extends ResetPasswordTest {

    @Test
    @Override
    public void givenForgotPasswordAndClickedOnCodeInMsg_thenGetDirectedToForgotPasswordPage() throws Exception {
        super.givenForgotPasswordAndClickedOnCodeInMsg_thenGetDirectedToForgotPasswordPage();
    }
}
