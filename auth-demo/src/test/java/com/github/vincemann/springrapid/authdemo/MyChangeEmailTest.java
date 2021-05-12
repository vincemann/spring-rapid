package com.github.vincemann.springrapid.authdemo;

import com.github.vincemann.springrapid.authdemo.adapter.EnableProjectComponentScan;
import com.github.vincemann.springrapid.authtests.ChangeEmailTest;
import org.junit.jupiter.api.Test;

@EnableProjectComponentScan
public class MyChangeEmailTest extends ChangeEmailTest {

    @Test
    @Override
    public void cantChangeOwnEmailWithInvalidCode() throws Exception {
        super.cantChangeOwnEmailWithInvalidCode();
    }
}
