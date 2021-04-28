package com.github.vincemann.springrapid.authtests.adapter;

import com.github.vincemann.springrapid.auth.domain.AbstractUser;

public interface AuthTestAdapter {


    public AbstractUser<Long> createTestUser(String email,String password, String... roles);



}
