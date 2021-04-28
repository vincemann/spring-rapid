package com.github.vincemann.springrapid.authdemo.adapter;

import com.github.vincemann.springrapid.auth.domain.AbstractUser;
import com.github.vincemann.springrapid.authtests.adapter.AuthTestAdapter;
import com.github.vincemann.springrapid.authdemo.model.User;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
public class MyAuthTestAdapter implements AuthTestAdapter {

    private int nameCount = 0;
    private static final String NAME = "testUserName";

    @Override
    public AbstractUser<Long> createTestUser(String email, String password, String... roles) {
        return new User(email,password,createUniqueName(),roles);
    }


    public String createUniqueName(){
        String name = NAME+nameCount;
        nameCount++;
        return name;
    }


}
