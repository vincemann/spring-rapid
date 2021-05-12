package com.github.vincemann.springrapid.authtests;

import com.github.vincemann.springrapid.acl.proxy.Acl;
import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.config.RapidAdminAutoConfiguration;
import com.github.vincemann.springrapid.auth.controller.AbstractUserController;
import com.github.vincemann.springrapid.auth.domain.AbstractUser;
import com.github.vincemann.springrapid.auth.domain.AuthRoles;
import com.github.vincemann.springrapid.auth.mail.MailSender;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.authtest.controller.UserIntegrationControllerTest;
import com.github.vincemann.springrapid.authtests.adapter.AuthTestAdapter;
import com.github.vincemann.springrapid.core.CoreProperties;
import com.github.vincemann.springrapid.coretest.util.RapidTestUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.util.AopTestUtils;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



//see application-dev.yml config for expected database config
//@Sql({"/test-data/resetTestData.sql"})

/**
 * Creates All Test Users.
 * Fills tokens Map in an integration test manner by creating and logging all users in
 */
//@Transactional - dont do transactional bc controller will be wrapped in transaction as well -> lazyLoad Exceptions ect. wont be detected
@SpringBootTest({
//        "logging.level.com.naturalprogrammer=ERROR", // logging.level.root=ERROR does not work: https://stackoverflow.com/questions/49048298/springboottest-not-overriding-logging-level
//        "logging.level.org.springframework=ERROR",
        "lemon.recaptcha.sitekey="
})
@ImportAutoConfiguration(exclude = RapidAdminAutoConfiguration.class)
@Getter
@Slf4j
public abstract class AbstractRapidAuthIntegrationTest
        extends UserIntegrationControllerTest<AbstractUserController> {

    protected static final String NEW_EMAIL = "new.email@example.com";
    protected static final String INVALID_EMAIL = "an-invalid-email";

    protected static final String ADMIN_EMAIL = "admin@example.com";
    protected static final String ADMIN_PASSWORD = "adminSanjaySanjay99!";

    protected static final String SECOND_ADMIN_EMAIL = "secondAdmin@example.com";
    protected static final String SECOND_ADMIN_PASSWORD = "secondAdminSanjaySanjay99!";

    protected static final String BLOCKED_ADMIN_EMAIL = "blockedAdmin@example.com";
    protected static final String BLOCKED_ADMIN_PASSWORD = "blockedAdminSanjaySanjay99!";

    protected static final String USER_EMAIL = "user@example.com";
    protected static final String USER_PASSWORD = "userSanjaySanjay99!";

    protected static final String SECOND_USER_EMAIL = "secondUser@example.com";
    protected static final String SECOND_USER_PASSWORD = "secondUserSanjaySanjay99!";

    protected static final String UNVERIFIED_USER_EMAIL = "unverifiedUser@example.com";
    protected static final String UNVERIFIED_USER_PASSWORD = "unverifiedUserSanjaySanjay99!";

    protected static final String BLOCKED_USER_EMAIL = "blockedUser@example.com";
    protected static final String BLOCKED_USER_PASSWORD = "blockedUserSanjaySanjay99!";

    protected static String UNKNOWN_USER_ID = "99";

    @Autowired
    @Acl
    private UserService<AbstractUser<Long>, Long> aclUserService;

    @Autowired
    private UserService<AbstractUser<Long>, Long> userService;

    @MockBean
    protected MailSender<?> mailSender;

    @Autowired
    protected DataSource dataSource;

    //use for stubbing i.E. Mockito.doReturn(mockedExpireTime).when(jwt).getExpirationMillis();
    @SpyBean
    protected AuthProperties properties;

    @SpyBean
    protected CoreProperties coreProperties;

    protected AuthProperties.Jwt jwt;

    protected Map<Long, String> tokens = new HashMap<>(6);

    protected AbstractUser<Long> admin;
    protected AbstractUser<Long> secondAdmin;
    protected AbstractUser<Long> blockedAdmin;
    protected AbstractUser<Long> user;
    protected AbstractUser<Long> secondUser;
    protected AbstractUser<Long> unverifiedUser;
    protected AbstractUser<Long> blockedUser;

    @Autowired
    protected AuthTestAdapter testAdapter;

    @Autowired
    protected AuthProperties authProperties;

    @BeforeEach
    protected void setup() throws Exception {
        System.err.println("creating test users");
        createTestUsers();
        System.err.println("test users created");
        System.err.println("logging in test users");
//        loginTestUsers();
        System.err.println("test users logged in");
        setupSpies();
        System.err.println("TEST STARTS HERE -----------------------------------------------------------------------------------------------------------------");
    }



    protected void setupSpies(){
        jwt = Mockito.spy(properties.getJwt());
        Mockito.doReturn(jwt).when(unproxy(properties)).getJwt();
    }

    protected <T> T unproxy(T spy){
        //        https://stackoverflow.com/questions/9033874/mocking-a-property-of-a-cglib-proxied-service-not-working
        return AopTestUtils.getUltimateTargetObject(spy);
    }



    @Override
    protected DefaultMockMvcBuilder createMvcBuilder() {
        DefaultMockMvcBuilder mvcBuilder = super.createMvcBuilder();
        mvcBuilder.apply(SecurityMockMvcConfigurers.springSecurity());
        return mvcBuilder;
    }



    protected void createTestUsers() throws Exception {
        admin = aclUserService.save(testAdapter.createTestUser(ADMIN_EMAIL,/*"Admin",*/ ADMIN_PASSWORD, AuthRoles.ADMIN));
        secondAdmin = aclUserService.save(testAdapter.createTestUser(SECOND_ADMIN_EMAIL,/*"Second Admin",*/ SECOND_ADMIN_PASSWORD, AuthRoles.ADMIN));
        blockedAdmin = aclUserService.save(testAdapter.createTestUser(BLOCKED_ADMIN_EMAIL,/*"Blocked Admin",*/ BLOCKED_ADMIN_PASSWORD, AuthRoles.ADMIN, AuthRoles.BLOCKED));

        user = aclUserService.save(testAdapter.createTestUser(USER_EMAIL,/*"User",*/ USER_PASSWORD, AuthRoles.USER));
        secondUser = aclUserService.save(testAdapter.createTestUser(SECOND_USER_EMAIL,/*"User",*/ SECOND_USER_PASSWORD, AuthRoles.USER));
        unverifiedUser = aclUserService.save(testAdapter.createTestUser(UNVERIFIED_USER_EMAIL,/*"Unverified User",*/ UNVERIFIED_USER_PASSWORD, AuthRoles.USER, AuthRoles.UNVERIFIED));
        blockedUser = aclUserService.save(testAdapter.createTestUser(BLOCKED_USER_EMAIL,/*"Blocked User",*/ BLOCKED_USER_PASSWORD, AuthRoles.USER, AuthRoles.BLOCKED));
        // sleep so login shortly after wont result in obsolete token
//        Thread.sleep(400);
    }



//    protected void loginTestUsers() throws Exception {
//        tokens.put(getAdmin().getId(), login2xx(ADMIN_EMAIL, ADMIN_PASSWORD));
//        tokens.put(getSecondAdmin().getId(), login2xx(SECOND_ADMIN_EMAIL, SECOND_ADMIN_PASSWORD));
//        tokens.put(getBlockedAdmin().getId(), login2xx(BLOCKED_ADMIN_EMAIL, BLOCKED_ADMIN_PASSWORD));
//
//        tokens.put(getUser().getId(), login2xx(USER_EMAIL, USER_PASSWORD));
//        tokens.put(getUnverifiedUser().getId(), login2xx(UNVERIFIED_USER_EMAIL, UNVERIFIED_USER_PASSWORD));
//        tokens.put(getBlockedUser().getId(), login2xx(BLOCKED_USER_EMAIL, BLOCKED_USER_PASSWORD));
//    }


    protected String login2xx(String username, String password, long expirationMillis) throws Exception {
        Mockito.doReturn(expirationMillis).when(jwt).getExpirationMillis();
        return login2xx(username,password);
    }

    protected void ensureTokenWorks(String token) throws Exception {
        mvc.perform(get(authProperties.getController().getContextUrl())
                .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().is(200));
//                .andExpect(jsonPath("$.user.id").value(getUnverifiedUser().getId()));
    }

    @AfterEach
    protected void tearDown() throws SQLException {
        System.err.println("TEST ENDS HERE -----------------------------------------------------------------------------------------------------------------");
        System.err.println("clearing test data");
        tokens.clear();
        System.err.println("deleting users");
        RapidTestUtil.clear(aclUserService);
        System.err.println("deleted users");
        System.err.println("test data cleared");

        Mockito.reset(unproxy(mailSender));
//        https://github.com/spring-projects/spring-boot/issues/7374  -> @SpyBean beans are automatically reset

//        Mockito.reset(properties);
//        Mockito.reset(coreProperties);
//        Mockito.reset(jwt);
    }



}
