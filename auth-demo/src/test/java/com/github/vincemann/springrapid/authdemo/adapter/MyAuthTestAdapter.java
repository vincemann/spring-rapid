package com.github.vincemann.springrapid.authdemo.adapter;

import com.github.vincemann.springrapid.auth.domain.AbstractUser;
import com.github.vincemann.springrapid.auth.domain.dto.SignupForm;
import com.github.vincemann.springrapid.authtests.adapter.AuthTestAdapter;
import com.github.vincemann.springrapid.authdemo.domain.MySignupForm;
import com.github.vincemann.springrapid.authdemo.domain.User;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
public class MyAuthTestAdapter implements AuthTestAdapter {

    private int nameCount = 0;
    private static final String NAME = "testUserName";

    @Override
    public AbstractUser<Long> createTestUser(String email, String password, String... roles) {
        return new User(email,password,createUniqueName(),roles);
    }

    @Override
    public SignupForm createSignupForm(String email, String password) {
        return new MySignupForm(email,password,createUniqueName());
    }

    private String createUniqueName(){
        String name = NAME+nameCount;
        nameCount++;
        return name;
    }

    @Override
    public String getUpdatableUserField() {
        return "name";
    }
}
