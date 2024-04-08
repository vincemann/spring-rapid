package com.github.vincemann.springrapid.authtests;

import com.github.vincemann.springrapid.auth.*;
import com.github.vincemann.springrapid.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.Set;

public abstract class AuthTestAdapter {

    public static  String NEW_CONTACT_INFORMATION = "new.contactInformation@example.com";
    public static  String NEW_PASSWORD = "newPasswordSanjaySanjay99!";
    public static  String INVALID_CONTACT_INFORMATION = "an-invalid-contactInformation";
    public static  String INVALID_PASSWORD = "short";
    public static  String UNKNOWN_CONTACT_INFORMATION = "unknown@example.com";
    public static String UNKNOWN_USER_ID = "99";

    public static String WRONG_PASSWORD = "myWrongPass325+346ö+7";

    // admins
    public static  String ADMIN_CONTACT_INFORMATION = "admin@example.com";
    public static  String ADMIN_PASSWORD = "adminSanjaySanjay99!";
    public static  String BLOCKED_ADMIN_CONTACT_INFORMATION = "blockedAdmin@example.com";
    public static  String BLOCKED_ADMIN_PASSWORD = "blockedAdminSanjaySanjay99!";

    // users

    public static  String USER_CONTACT_INFORMATION = "user@example.com";
    public static  String USER_PASSWORD = "1Best??pw==";

    public static  String SECOND_USER_CONTACT_INFORMATION = "secondUser@example.com";
    public static  String SECOND_USER_PASSWORD = "secondUserSanjaySanjay99!";

    // unverified
    public static  String UNVERIFIED_USER_CONTACT_INFORMATION = "unverifiedUser@example.com";
    public static  String UNVERIFIED_USER_PASSWORD = "unverifiedUserSanjaySanjay99!";

    // blocked
    public static  String BLOCKED_USER_CONTACT_INFORMATION = "blockedUser@example.com";
    public static  String BLOCKED_USER_PASSWORD = "blockedUserSanjaySanjay99!";

    private UserService userService;
    private AbstractUserRepository userRepository;


    public void beforeEach() throws Exception{}

    public void afterEach()throws Exception {}

    protected AbstractUser createTestUser(String contactInformation, String password, String... roles){
        AbstractUser user = userService.createUser();
        user.setContactInformation(contactInformation);
        user.setPassword(password);
        user.setRoles(Set.of(roles));
        return user;
    }

    public abstract AbstractUser<?> signupUser() throws Exception;

    public AbstractUser<?> createUser() throws BadEntityException {
        return userService.create(createTestUser(USER_CONTACT_INFORMATION,USER_PASSWORD, Roles.USER));
    }

    public AbstractUser<?> createSecondUser() throws BadEntityException {
        return userService.create(createTestUser(SECOND_USER_CONTACT_INFORMATION,SECOND_USER_PASSWORD, Roles.USER));
    }

    public AbstractUser<?> createBlockedUser() throws BadEntityException {
        return userService.create(createTestUser(BLOCKED_USER_CONTACT_INFORMATION,BLOCKED_USER_PASSWORD, Roles.USER, Roles.BLOCKED));
    }

    public AbstractUser<?> createUnverifiedUser() throws BadEntityException {
        return userService.create(createTestUser(UNVERIFIED_USER_CONTACT_INFORMATION,UNVERIFIED_USER_PASSWORD, Roles.USER, Roles.UNVERIFIED));
    }

    public AbstractUser<?> createBlockedAdmin() throws BadEntityException {
        return userService.create(createTestUser(BLOCKED_ADMIN_CONTACT_INFORMATION,BLOCKED_ADMIN_PASSWORD, Roles.ADMIN, Roles.BLOCKED));
    }

    public AbstractUser<?> createAdmin() throws BadEntityException {
        return userService.create(createTestUser(ADMIN_CONTACT_INFORMATION,ADMIN_PASSWORD, Roles.ADMIN));
    }

    public AbstractUser<?> fetchUser(String contactInformation){
        Optional<AbstractUser<?>> found = userRepository.findByContactInformation(contactInformation);
        if (found.isEmpty())
            throw new IllegalArgumentException("user not found in db with contact information: " +contactInformation);
        return found.get();
    }

    @Autowired
    public void setUserRepository(AbstractUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    @Root
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
