package com.github.vincemann.springrapid.authtests.adapter;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.dto.SignupDto;
import com.github.vincemann.springrapid.core.sec.Roles;
import com.google.common.collect.Sets;

public abstract class AuthTestAdapter {

    public static  String SIGNUP_USER_CONTACT_INFORMATION = "signupUser@example.com";
    public static  String SIGNUP_USER_PASSWORD = "signupUserSanjaySanjay99!";
    public static  String NEW_CONTACT_INFORMATION = "new.contactInformation@example.com";
    public static  String NEW_PASSWORD = "newPasswordSanjaySanjay99!";
    public static  String INVALID_CONTACT_INFORMATION = "an-invalid-contactInformation";
    public static  String INVALID_PASSWORD = "short";
    public static  String UNKNOWN_CONTACT_INFORMATION = "unknown@example.com";

    public static  String ADMIN_CONTACT_INFORMATION = "admin@example.com";
    public static  String ADMIN_PASSWORD = "adminSanjaySanjay99!";

    public static  String SECOND_ADMIN_CONTACT_INFORMATION = "secondAdmin@example.com";
    public static  String SECOND_ADMIN_PASSWORD = "secondAdminSanjaySanjay99!";

    public static  String BLOCKED_ADMIN_CONTACT_INFORMATION = "blockedAdmin@example.com";
    public static  String BLOCKED_ADMIN_PASSWORD = "blockedAdminSanjaySanjay99!";

    public static  String USER_CONTACT_INFORMATION = "user@example.com";
    public static  String USER_PASSWORD = "userSanjaySanjay99!";

    public static  String SECOND_USER_CONTACT_INFORMATION = "secondUser@example.com";
    public static  String SECOND_USER_PASSWORD = "secondUserSanjaySanjay99!";

    public static  String UNVERIFIED_USER_CONTACT_INFORMATION = "unverifiedUser@example.com";
    public static  String UNVERIFIED_USER_PASSWORD = "unverifiedUserSanjaySanjay99!";

    public static  String BLOCKED_USER_CONTACT_INFORMATION = "blockedUser@example.com";
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

    public abstract AbstractUser createTestUser(String contactInformation, String password, String... roles);

    public SignupDto createValidSignupDto(){
        return new SignupDto(SIGNUP_USER_CONTACT_INFORMATION, SIGNUP_USER_PASSWORD, Sets.newHashSet(Roles.USER));
    }

    public SignupDto createInvalidSignupDto(){
        return new SignupDto(INVALID_CONTACT_INFORMATION,INVALID_PASSWORD,Sets.newHashSet(Roles.USER));
    }




}
