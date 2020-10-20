package com.github.vincemann.springlemon.demo.adapter;

import com.fasterxml.jackson.annotation.JsonView;
import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.domain.dto.SignupForm;
import com.github.vincemann.springlemon.auth.util.UserVerifyUtils;
import com.github.vincemann.springlemon.authtests.adapter.LemonTestAdapter;
import com.github.vincemann.springlemon.demo.domain.MyUser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
public class MyLemonTestAdapter implements LemonTestAdapter {

    private int nameCount = 0;
    private static final String NAME = "testUserName";

    @NoArgsConstructor
    @Getter
    static class MySignupForm extends SignupForm {
        @JsonView(UserVerifyUtils.SignupInput.class)
        private String name;

        public MySignupForm(String email, String password, String name) {
            super(email, password);
            this.name = name;
        }
    }

    @Override
    public AbstractUser<Long> createTestUser(String email, String password, String... roles) {
        return new MyUser(email,password,createUniqueName(),roles);
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
