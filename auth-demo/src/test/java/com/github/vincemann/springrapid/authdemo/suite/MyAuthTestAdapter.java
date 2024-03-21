package com.github.vincemann.springrapid.authdemo.suite;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.dto.SignupDto;
import com.github.vincemann.springrapid.authdemo.dto.SignupDto;
import com.github.vincemann.springrapid.authtests.AuthTestAdapter;
import com.github.vincemann.springrapid.authdemo.model.User;

public class MyAuthTestAdapter extends AuthTestAdapter {

    private int nameCount = 0;
    private static final String NAME = "testUserName";

    @Override
    public AbstractUser<Long> createTestUser(String contactInformation, String password, String... roles) {
        return new User(contactInformation,password,createUniqueName(),roles);
    }

    @Override
    public SignupDto createValidSignupDto() {
        SignupDto signupDto = super.createValidSignupDto();
        return SignupDto.Builder()
                .name(createUniqueName())
                .contactInformation(signupDto.getContactInformation())
                .password(signupDto.getPassword())
                .build();
    }

    @Override
    public SignupDto createInvalidSignupDto() {
        SignupDto signupDto = super.createInvalidSignupDto();
        return SignupDto.Builder()
                .name(createUniqueName())
                .contactInformation(signupDto.getContactInformation())
                .password(signupDto.getPassword())
                .build();
    }


    private String createUniqueName(){
        return NAME+nameCount++;
    }

}
