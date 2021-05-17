package com.github.vincemann.springrapid.authdemo;

import com.github.vincemann.springrapid.authdemo.adapter.EnableProjectComponentScan;
import com.github.vincemann.springrapid.authtests.ChangePasswordTest;
import org.junit.jupiter.api.Test;

@EnableProjectComponentScan
public class MyChangePasswordTest extends ChangePasswordTest {

    @Test
    @Override
    public void canChangeOwnPassword() throws Exception {
        super.canChangeOwnPassword();
    }

    @Test
    @Override
    public void cantChangePasswordForUnknownId() throws Exception {
        super.cantChangePasswordForUnknownId();
    }

    @Test
    @Override
    public void cantChangeOwnPasswordWithInvalidData() throws Exception {
        super.cantChangeOwnPasswordWithInvalidData();
    }
}
