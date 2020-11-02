package com.github.vincemann.springrapid.authdemo;

import com.github.vincemann.springrapid.auth.domain.dto.SignupForm;
import com.github.vincemann.springrapid.authdemo.adapter.MyAuthTestAdapter;
import com.github.vincemann.springrapid.authdemo.config.MyUserServiceConfig;
import com.github.vincemann.springrapid.authdemo.domain.MySignupForm;
import com.github.vincemann.springrapid.authtests.SignupMvcTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@ComponentScan("com.github.vincemann.springrapid.authdemo")
@Import(MyUserServiceConfig.class)
public class MySignupMvcTests extends SignupMvcTests {

    @Autowired
    private MyAuthTestAdapter testAdapter;

    @Override
    protected SignupForm createValidSignupForm() {
        SignupForm signupForm = super.createValidSignupForm();
        return new MySignupForm(signupForm.getEmail(),signupForm.getPassword(),testAdapter.createUniqueName());
    }

    @Override
    protected SignupForm createInvalidSignupForm() {
        SignupForm signupForm = super.createInvalidSignupForm();
        return new MySignupForm(signupForm.getEmail(),signupForm.getPassword(),testAdapter.createUniqueName());
    }
}
