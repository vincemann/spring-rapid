package com.github.vincemann.springrapid.authdemo;

import com.github.vincemann.springrapid.auth.domain.dto.SignupDto;
import com.github.vincemann.springrapid.authdemo.adapter.EnableProjectComponentScan;
import com.github.vincemann.springrapid.authdemo.adapter.MyAuthTestAdapter;
import com.github.vincemann.springrapid.authdemo.config.UserServiceConfig;
import com.github.vincemann.springrapid.authdemo.dto.MySignupDto;
import com.github.vincemann.springrapid.authtests.SignupTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@EnableProjectComponentScan
@Import(UserServiceConfig.class)
public class MySignupTest extends SignupTest {

    @Autowired
    private MyAuthTestAdapter testAdapter;

    @Override
    protected SignupDto createValidSignupForm() {
        SignupDto signupDto = super.createValidSignupForm();
        return new MySignupDto(signupDto.getEmail(), signupDto.getPassword(),testAdapter.createUniqueName());
    }

    @Override
    protected SignupDto createInvalidSignupForm() {
        SignupDto signupDto = super.createInvalidSignupForm();
        return new MySignupDto(signupDto.getEmail(), signupDto.getPassword(),testAdapter.createUniqueName());
    }
}