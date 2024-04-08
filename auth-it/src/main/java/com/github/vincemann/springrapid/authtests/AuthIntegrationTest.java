package com.github.vincemann.springrapid.authtests;

import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.AbstractUserRepository;
import com.github.vincemann.springrapid.auth.msg.AuthMessage;
import com.github.vincemann.springrapid.auth.msg.MessageSender;
import com.github.vincemann.springrapid.auth.jwt.BadTokenException;
import com.github.vincemann.springrapid.auth.jwt.JweTokenService;
import com.github.vincemann.springrapid.auth.util.JwtUtils;
import com.github.vincemann.springrapid.authtest.AbstractUserControllerTestTemplate;

import com.github.vincemann.springrapid.auth.util.AopProxyUtils;
import com.nimbusds.jwt.JWTClaimsSet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.util.AopTestUtils;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(properties = "rapid-auth.create-admins=false")
@Sql(scripts = "classpath:/remove-acl-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public abstract class AuthIntegrationTest extends AbstractMvcTest {

    @MockBean
    protected MessageSender msgSender;


    // use for stubbing i.E. Mockito.doReturn(mockedExpireTime).when(jwt).getExpirationMillis();
    @SpyBean
    protected AuthProperties properties;

    protected AuthProperties.Jwt jwt;

    @Autowired
    protected JweTokenService jweTokenService;

    @Autowired
    protected AuthTestAdapter testAdapter;

    @Autowired
    protected AbstractUserControllerTestTemplate<?> userController;

    @Autowired
    protected TransactionTemplate transactionTemplate;

    @Autowired
    protected AbstractUserRepository userRepository;

    @Autowired
    private AclCache aclCache;

    @BeforeEach
    protected void setup() throws Exception {
        testAdapter.beforeEach();
        setupSpies();
    }

    protected void setupSpies() {
        jwt = Mockito.spy(properties.getJwt());
        AuthProperties properties = AopTestUtils.getUltimateTargetObject(this.properties);
        Mockito.doReturn(jwt)
                .when(properties)
                .getJwt();
    }


    @Override
    protected DefaultMockMvcBuilder createMvcBuilder() {
        DefaultMockMvcBuilder mvcBuilder = super.createMvcBuilder();
        mvcBuilder.apply(SecurityMockMvcConfigurers.springSecurity());
        return mvcBuilder;
    }




    protected void mockJwtExpirationTime(long expirationMillis) {
        Mockito.doReturn(expirationMillis).when(jwt).getExpirationMillis();
    }

    protected void assertTokenWorks(String token, Serializable id) throws Exception {
        mvc.perform(get(properties.getController().getTestTokenUrl())
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().is2xxSuccessful());
    }

    protected void assertTokenDoesNotWork(String token) throws Exception {
        mvc.perform(get(properties.getController().getTestTokenUrl())
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isUnauthorized());
    }

    public AuthMessage verifyMsgWasSent(String recipient) {
        ArgumentCaptor<AuthMessage> msgCaptor = ArgumentCaptor.forClass(AuthMessage.class);

        verify(AopProxyUtils.unproxy(msgSender), atLeast(1))
                .send(msgCaptor.capture());
        AuthMessage sentData = msgCaptor.getValue();
        assertThat("latest msg must be sent to recipient: " +recipient + " but was sent to: " + sentData.getRecipient(),
                sentData.getRecipient(),equalTo(recipient));
        Mockito.reset(AopProxyUtils.unproxy(msgSender));
        return sentData;
    }

    protected String modifyCode(String code, String aud, String subject, Long expirationMillis, Long issuedAt, Map<String, Object> otherClaims) throws BadTokenException, ParseException {
        JWTClaimsSet claims = jweTokenService.parseToken(code);
        claims = JwtUtils.mod(claims, aud, subject, expirationMillis, issuedAt, otherClaims);
        return jweTokenService.createToken(claims);
    }

    protected void verifyNoMsgSent(){
        verify(AopProxyUtils.unproxy(msgSender), never()).send(any());
    }

    @AfterEach
    protected void tearDown() throws Exception {
        // dont remove users via sql script, because its db impl specific - use service
        testAdapter.afterEach();
        removeTestUsers();
        Mockito.reset(AopProxyUtils.unproxy(msgSender));
        aclCache.clearCache();
    }

    protected void removeTestUsers(){
        TransactionalTestUtil.clear(userRepository, transactionTemplate);
    }
}

