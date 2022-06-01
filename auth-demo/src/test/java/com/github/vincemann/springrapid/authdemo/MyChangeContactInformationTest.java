package com.github.vincemann.springrapid.authdemo;

import com.github.vincemann.springrapid.authdemo.adapter.EnableProjectComponentScan;
import com.github.vincemann.springrapid.authtests.ChangeContactInformationTest;
import org.junit.jupiter.api.Test;

@EnableProjectComponentScan
public class MyChangeContactInformationTest extends ChangeContactInformationTest {

    @Test
    @Override
    public void canChangeOwnContactInformation() throws Exception {
        super.canChangeOwnContactInformation();
    }
}
