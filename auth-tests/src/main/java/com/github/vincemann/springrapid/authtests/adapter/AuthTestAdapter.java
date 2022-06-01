package com.github.vincemann.springrapid.authtests.adapter;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.dto.SignupDto;

public abstract class AuthTestAdapter {

    public static  String SIGNUP_USER_EMAIL = "signupUser@example.com";
    public static  String SIGNUP_USER_PASSWORD = "signupUserSanjaySanjay99!";
    public static  String NEW_EMAIL = "new.contactInformation@example.com";
    public static  String NEW_PASSWORD = "newPasswordSanjaySanjay99!";
    public static  String INVALID_EMAIL = "an-invalid-contactInformation";
    public static  String INVALID_PASSWORD = "short";
    public static  String UNKNOWN_EMAIL = "unknown@example.com";

    public static  String ADMIN_EMAIL = "admin@example.com";
    public static  String ADMIN_PASSWORD = "adminSanjaySanjay99!";

    public static  String SECOND_ADMIN_EMAIL = "secondAdmin@example.com";
    public static  String SECOND_ADMIN_PASSWORD = "secondAdminSanjaySanjay99!";

    public static  String BLOCKED_ADMIN_EMAIL = "blockedAdmin@example.com";
    public static  String BLOCKED_ADMIN_PASSWORD = "blockedAdminSanjaySanjay99!";

    public static  String USER_EMAIL = "user@example.com";
    public static  String USER_PASSWORD = "userSanjaySanjay99!";

    public static  String SECOND_USER_EMAIL = "secondUser@example.com";
    public static  String SECOND_USER_PASSWORD = "secondUserSanjaySanjay99!";

    public static  String UNVERIFIED_USER_EMAIL = "unverifiedUser@example.com";
    public static  String UNVERIFIED_USER_PASSWORD = "unverifiedUserSanjaySanjay99!";

    public static  String BLOCKED_USER_EMAIL = "blockedUser@example.com";
    public static  String BLOCKED_USER_PASSWORD = "blockedUserSanjaySanjay99!";

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

    public void beforeEach() throws Exception{}

    public void afterEach()throws Exception {}

    public abstract AbstractUser<Long> createTestUser(String contactInformation, String password, String... roles);

    public SignupDto createValidSignupDto(){
        return new SignupDto(SIGNUP_USER_EMAIL, SIGNUP_USER_PASSWORD);
    }

    public SignupDto createInvalidSignupDto(){
        return new SignupDto(INVALID_EMAIL,INVALID_PASSWORD);
    }




}
