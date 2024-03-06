package com.github.vincemann.springrapid.authtests;

import com.github.vincemann.acltest.AclMvcTest;

import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.auth.dto.SignupDto;
import com.github.vincemann.springrapid.auth.msg.MessageSender;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.auth.service.token.BadTokenException;
import com.github.vincemann.springrapid.auth.service.token.JweTokenService;
import com.github.vincemann.springrapid.auth.util.JwtUtils;
import com.github.vincemann.springrapid.authtest.UserControllerTestTemplate;
import com.github.vincemann.springrapid.authtests.adapter.AuthTestAdapter;
import com.github.vincemann.springrapid.core.CoreProperties;
import com.github.vincemann.springrapid.core.Root;
import com.github.vincemann.springrapid.coretest.util.TransactionalTestUtil;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Map;

import static com.github.vincemann.springrapid.authtests.adapter.AuthTestAdapter.*;
import static com.github.vincemann.springrapid.core.util.HibernateProxyUtils.getAopUltimateTargetObject;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(properties = "rapid-auth.create-admins=false")
@Getter
@Slf4j
//@Sql(scripts = "classpath:/remove-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD) // dont do like that because its db impl specific - use service
public abstract class RapidAuthIntegrationTest extends AclMvcTest {



    private UserService<AbstractUser<Serializable>, Serializable> userService;

    @MockBean
    protected MessageSender msgSender;


    //use for stubbing i.E. Mockito.doReturn(mockedExpireTime).when(jwt).getExpirationMillis();
    @SpyBean
    protected AuthProperties properties;

    @SpyBean
    protected CoreProperties coreProperties;

    protected AuthProperties.Jwt jwt;

    @Autowired
    protected JweTokenService jweTokenService;


    protected AbstractUser<Serializable> admin;
    protected AbstractUser<Serializable> secondAdmin;
    protected AbstractUser<Serializable> blockedAdmin;
    protected AbstractUser<Serializable> user;
    protected AbstractUser<Serializable> secondUser;
    protected AbstractUser<Serializable> unverifiedUser;
    protected AbstractUser<Serializable> blockedUser;

    @Autowired
    protected AuthTestAdapter testAdapter;

    @Autowired
    protected AuthProperties authProperties;

    @Autowired
    protected UserControllerTestTemplate userController;

    @Autowired
    protected TransactionTemplate transactionTemplate;

    @BeforeEach
    protected void setup() throws Exception {
        testAdapter.beforeEach();
        System.err.println("creating test users");
        createTestUsers();
        System.err.println("test users created");
        System.err.println("logging in test users");
//        loginTestUsers();
        System.err.println("test users logged in");
        setupSpies();
        System.err.println("TEST STARTS HERE -----------------------------------------------------------------------------------------------------------------");
    }

    protected void setupSpies() {
        jwt = Mockito.spy(properties.getJwt());
        Mockito.doReturn(jwt).when(getAopUltimateTargetObject(properties)).getJwt();
    }


    @Override
    protected DefaultMockMvcBuilder createMvcBuilder() {
        DefaultMockMvcBuilder mvcBuilder = super.createMvcBuilder();
        mvcBuilder.apply(SecurityMockMvcConfigurers.springSecurity());
        return mvcBuilder;
    }


    protected void createTestUsers() throws Exception {
        admin = userService.create(testAdapter.createTestUser(ADMIN_CONTACT_INFORMATION,/*"Admin",*/ ADMIN_PASSWORD, AuthRoles.ADMIN));
        secondAdmin = userService.create(testAdapter.createTestUser(SECOND_ADMIN_CONTACT_INFORMATION,/*"Second Admin",*/ SECOND_ADMIN_PASSWORD, AuthRoles.ADMIN));
        blockedAdmin = userService.create(testAdapter.createTestUser(BLOCKED_ADMIN_CONTACT_INFORMATION,/*"Blocked Admin",*/ BLOCKED_ADMIN_PASSWORD, AuthRoles.ADMIN, AuthRoles.BLOCKED));

        user = userService.create(testAdapter.createTestUser(USER_CONTACT_INFORMATION,/*"User",*/ USER_PASSWORD, AuthRoles.USER));
        secondUser = userService.create(testAdapter.createTestUser(SECOND_USER_CONTACT_INFORMATION,/*"User",*/ SECOND_USER_PASSWORD, AuthRoles.USER));
        unverifiedUser = userService.create(testAdapter.createTestUser(UNVERIFIED_USER_CONTACT_INFORMATION,/*"Unverified User",*/ UNVERIFIED_USER_PASSWORD, AuthRoles.USER, AuthRoles.UNVERIFIED));
        blockedUser = userService.create(testAdapter.createTestUser(BLOCKED_USER_CONTACT_INFORMATION,/*"Blocked User",*/ BLOCKED_USER_PASSWORD, AuthRoles.USER, AuthRoles.BLOCKED));
    }

    protected void mockJwtExpirationTime(long expirationMillis) {
        Mockito.doReturn(expirationMillis).when(jwt).getExpirationMillis();
    }

    protected String login2xx(String username, String password, long expirationMillis) throws Exception {
        mockJwtExpirationTime(expirationMillis);
        return userController.login2xx(username, password);
    }

    protected String login2xx(String username, String password) throws Exception {
        return userController.login2xx(username, password);
    }

    protected void assertTokenWorks(String token, Serializable id) throws Exception {
        mvc.perform(get(authProperties.getController().getTestTokenUrl())
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().is2xxSuccessful());
    }

    protected void assertTokenDoesNotWork(String token) throws Exception {
        mvc.perform(get(authProperties.getController().getTestTokenUrl())
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isUnauthorized());
    }

    protected SignupDto createValidSignupDto() {
        return testAdapter.createValidSignupDto();
    }


    protected SignupDto createInvalidSignupDto() {
        return testAdapter.createInvalidSignupDto();
    }

    protected String modifyCode(String code, String aud, String subject, Long expirationMillis, Long issuedAt, Map<String, Object> otherClaims) throws BadTokenException, ParseException {
        JWTClaimsSet claims = jweTokenService.parseToken(code);
        claims = JwtUtils.mod(claims, aud, subject, expirationMillis, issuedAt, otherClaims);
        return jweTokenService.createToken(claims);
    }

    @AfterEach
    protected void tearDown() throws Exception {
        System.err.println("TEST ENDS HERE -----------------------------------------------------------------------------------------------------------------");
        System.err.println("deleting users");
        clearAclCache();
        TransactionalTestUtil.clear(userService, transactionTemplate);
        System.err.println("deleted users");

        Mockito.reset(getAopUltimateTargetObject(msgSender));
        testAdapter.afterEach();
//        https://github.com/spring-projects/spring-boot/issues/7374  -> @SpyBean beans are automatically reset
    }

    @Autowired
    @Root
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}

