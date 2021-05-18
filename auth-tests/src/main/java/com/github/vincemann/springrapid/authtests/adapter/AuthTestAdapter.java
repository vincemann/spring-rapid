package com.github.vincemann.springrapid.authtests.adapter;

import com.github.vincemann.springrapid.auth.domain.AbstractUser;
import com.github.vincemann.springrapid.auth.domain.dto.SignupDto;

public abstract class AuthTestAdapter {

    public static final String SIGNUP_USER_EMAIL = "signupUser@example.com";
    public static final String SIGNUP_USER_PASSWORD = "signupUserSanjaySanjay99!";
    public static final String NEW_EMAIL = "new.email@example.com";
    public static final String NEW_PASSWORD = "newPasswordSanjaySanjay99!";
    public static final String INVALID_EMAIL = "an-invalid-email";
    public static final String INVALID_PASSWORD = "short";
    public static final String UNKNOWN_EMAIL = "unknown@example.com";

    public static final String ADMIN_EMAIL = "admin@example.com";
    public static final String ADMIN_PASSWORD = "adminSanjaySanjay99!";

    public static final String SECOND_ADMIN_EMAIL = "secondAdmin@example.com";
    public static final String SECOND_ADMIN_PASSWORD = "secondAdminSanjaySanjay99!";

    public static final String BLOCKED_ADMIN_EMAIL = "blockedAdmin@example.com";
    public static final String BLOCKED_ADMIN_PASSWORD = "blockedAdminSanjaySanjay99!";

    public static final String USER_EMAIL = "user@example.com";
    public static final String USER_PASSWORD = "userSanjaySanjay99!";

    public static final String SECOND_USER_EMAIL = "secondUser@example.com";
    public static final String SECOND_USER_PASSWORD = "secondUserSanjaySanjay99!";

    public static final String UNVERIFIED_USER_EMAIL = "unverifiedUser@example.com";
    public static final String UNVERIFIED_USER_PASSWORD = "unverifiedUserSanjaySanjay99!";

    public static final String BLOCKED_USER_EMAIL = "blockedUser@example.com";
    public static final String BLOCKED_USER_PASSWORD = "blockedUserSanjaySanjay99!";

    public static String UNKNOWN_USER_ID = "99";


    public String getUpdatableFieldName(){
        return null;
    }

    public String getNewValidFieldValue(){
        return null;
    }

    public String getInvalidFieldValue(){
        return null;
    }

    public abstract AbstractUser<Long> createTestUser(String email,String password, String... roles);

    public SignupDto createValidSignupDto(){
        return new SignupDto(SIGNUP_USER_EMAIL, SIGNUP_USER_PASSWORD);
    }

    public SignupDto createInvalidSignupDto(){
        return new SignupDto(INVALID_EMAIL,INVALID_PASSWORD);
    }




}
