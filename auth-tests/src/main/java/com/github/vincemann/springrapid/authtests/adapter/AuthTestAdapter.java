package com.github.vincemann.springrapid.authtests.adapter;

import com.github.vincemann.springrapid.auth.domain.AbstractUser;
import com.github.vincemann.springrapid.auth.domain.dto.SignupForm;

public interface AuthTestAdapter {


    public AbstractUser<Long> createTestUser(String email,String password, String... roles);

    /**
     * Constraints on field:
     * String
     * Not blank
     * Length 1 - 50
     * Not null
     * "newName" is valid new value
     */
    public String getUpdatableUserField();

    public default SignupForm createSignupForm(String email, String password){
        return new SignupForm(email,password);
    }
}
