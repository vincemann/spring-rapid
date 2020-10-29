package com.github.vincemann.springrapid.authtests.adapter;

import com.github.vincemann.springrapid.auth.domain.AbstractUser;
import com.github.vincemann.springrapid.auth.domain.dto.SignupForm;

public interface AuthTestAdapter {


    public AbstractUser<Long> createTestUser(String email,String password, String... roles);



}
