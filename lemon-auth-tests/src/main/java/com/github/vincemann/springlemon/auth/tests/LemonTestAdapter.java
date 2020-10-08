package com.github.vincemann.springlemon.auth.tests;

import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.domain.dto.LemonSignupForm;

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

    public default LemonSignupForm createSignupForm(String email, String password){
        return new LemonSignupForm(email,password);
    }
}
