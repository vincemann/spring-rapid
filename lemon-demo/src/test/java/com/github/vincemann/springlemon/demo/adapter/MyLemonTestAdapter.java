package com.github.vincemann.springlemon.demo.adapter;

import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.authtests.adapter.LemonTestAdapter;
import com.github.vincemann.springlemon.demo.domain.User;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
public class MyLemonTestAdapter implements LemonTestAdapter {

    private int nameCount = 0;
    private static final String NAME = "testUserName";

    @Override
    public AbstractUser<Long> createTestUser(String email, String password, String... roles) {
        String name = NAME+nameCount;
        nameCount++;
        return new User(email,password,name,roles);
    }

    @Override
    public String getUpdatableUserField() {
        return "name";
    }
}
