package com.github.vincemann.springrapid.authdemo;

import com.github.vincemann.springrapid.auth.domain.dto.SignupForm;
import com.github.vincemann.springrapid.authdemo.adapter.EnableProjectComponentScan;
import com.github.vincemann.springrapid.authdemo.adapter.MyAuthTestAdapter;
import com.github.vincemann.springrapid.authdemo.config.MyUserServiceConfig;
import com.github.vincemann.springrapid.authdemo.domain.MySignupForm;
import com.github.vincemann.springrapid.authtests.SignupAuthTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@EnableProjectComponentScan
@Import(MyUserServiceConfig.class)
public class MySignupAuthTest extends SignupAuthTest {

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
