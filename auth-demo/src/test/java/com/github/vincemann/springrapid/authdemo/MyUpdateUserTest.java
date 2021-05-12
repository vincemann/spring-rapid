package com.github.vincemann.springrapid.authdemo;

import com.github.vincemann.springrapid.authdemo.adapter.EnableProjectComponentScan;
import com.github.vincemann.springrapid.authtests.UpdateUserTest;

@EnableProjectComponentScan
public class MyUpdateUserTest extends UpdateUserTest {

    @Override
    protected String getUpdatableUserField() {
        return "name";
    }
}
