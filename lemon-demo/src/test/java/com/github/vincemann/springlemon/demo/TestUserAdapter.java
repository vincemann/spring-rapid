package com.github.vincemann.springlemon.demo;

import com.github.vincemann.springlemon.auth.domain.AbstractUser;

public interface TestUserAdapter {


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
}
