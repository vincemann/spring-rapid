package com.github.vincemann.springrapid.authdemo.adapter;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.dto.SignupDto;
import com.github.vincemann.springrapid.authdemo.dto.MySignupDto;
import com.github.vincemann.springrapid.authtests.adapter.AuthTestAdapter;
import com.github.vincemann.springrapid.authdemo.model.User;
import com.github.vincemann.springrapid.core.security.Roles;
import com.github.vincemann.springrapid.core.util.Lists;
import com.google.common.collect.Sets;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
public class MyAuthTestAdapter extends AuthTestAdapter {

    private int nameCount = 0;
    private static final String NAME = "testUserName";

    @Override
    public AbstractUser<Long> createTestUser(String contactInformation, String password, String... roles) {
        return new User(contactInformation,password,createUniqueName(),roles);
    }


    public String createUniqueName(){
        String name = NAME+nameCount;
        nameCount++;
        return name;
    }

    @Override
    public SignupDto createValidSignupDto() {
        SignupDto signupDto = super.createValidSignupDto();
        return new MySignupDto(signupDto.getContactInformation(), signupDto.getPassword(),createUniqueName(), Sets.newHashSet(Roles.USER));
    }

    @Override
    public String getUpdatableFieldName() {
        return "name";
    }

    @Override
    public String getNewValidFieldValue() {
        return "newSuperName";
    }

    @Override
    public String getInvalidFieldValue() {
        // too long
        return "A123456789A123456789A123456789A123456789A123456789A123456789A123456789";
    }

    @Override
    public SignupDto createInvalidSignupDto() {
        SignupDto signupDto = super.createInvalidSignupDto();
        return new MySignupDto(signupDto.getContactInformation(), signupDto.getPassword(),createUniqueName(),signupDto.getRoles());
    }

}
