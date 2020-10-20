package com.github.vincemann.springlemon.authtests.adapter;

import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.domain.dto.SignupForm;

public interface LemonTestAdapter {


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
