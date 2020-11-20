package com.github.vincemann.springrapid.authdemo;

import com.github.vincemann.springrapid.authdemo.adapter.EnableProjectComponentScan;
import com.github.vincemann.springrapid.authtests.UpdateUserAuthTest;

@EnableProjectComponentScan
public class MyUpdateUserAuthTest extends UpdateUserAuthTest {

    @Override
    protected String getUpdatableUserField() {
        return "name";
    }
}
