package com.github.vincemann.springrapid.authdemo.suite.config;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.dto.SignupDto;
import com.github.vincemann.springrapid.authdemo.dto.MySignupDto;
import com.github.vincemann.springrapid.authtests.adapter.AuthTestAdapter;
import com.github.vincemann.springrapid.authdemo.model.User;
import com.github.vincemann.springrapid.core.sec.Roles;
import com.google.common.collect.Sets;

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
        return new MySignupDto(signupDto.getContactInformation(), signupDto.getPassword(),createUniqueName(), Sets.newHashSet(Roles.USER));
    }

    @Override
    public SignupDto createInvalidSignupDto() {
        SignupDto signupDto = super.createInvalidSignupDto();
        return new MySignupDto(signupDto.getContactInformation(), signupDto.getPassword(),createUniqueName(),signupDto.getRoles());
    }


    private String createUniqueName(){
        return NAME+nameCount++;
    }

}
