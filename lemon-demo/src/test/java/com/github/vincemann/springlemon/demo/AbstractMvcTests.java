package com.github.vincemann.springlemon.demo;

import com.github.vincemann.springlemon.auth.config.LemonAdminAutoConfiguration;
import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.domain.AbstractUserRepository;
import com.github.vincemann.springlemon.auth.domain.LemonRoles;
import com.github.vincemann.springlemon.auth.mail.MailSender;
import com.github.vincemann.springlemon.auth.service.UserService;
import com.github.vincemann.springlemon.demo.domain.User;
import com.github.vincemann.springrapid.acl.proxy.AclManaging;
import com.github.vincemann.springrapid.core.security.RapidAuthenticatedPrincipal;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest({
//        "logging.level.com.naturalprogrammer=ERROR", // logging.level.root=ERROR does not work: https://stackoverflow.com/questions/49048298/springboottest-not-overriding-logging-level
//        "logging.level.org.springframework=ERROR",
        "lemon.recaptcha.sitekey="
})
//@AutoConfigureMockMvc

//@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.HSQL)
//see application-dev.yml config for expected database config
@Sql({"/test-data/resetTestData.sql"})
/**
 * Fills tokens Map in an integration test manner by logging all users in
 */
//just activate everything for simplicity
@ActiveProfiles(value = {"web", "service", "test", "webTest", "serviceTest", "dev"}, inheritProfiles = false)
@ImportAutoConfiguration(exclude = LemonAdminAutoConfiguration.class)
public abstract class AbstractMvcTests {

    protected static final long ADMIN_ID = 101L;
    protected static final long SECOND_ADMIN_ID = 102L;
    protected static final long BLOCKED_ADMIN_ID = 103L;

    protected static final long USER_ID = 104L;
    protected static final long UNVERIFIED_USER_ID = 105L;
    protected static final long BLOCKED_USER_ID = 106L;

    protected static final String ADMIN_EMAIL = "admin@example.com";
    protected static final String ADMIN_PASSWORD = "admin!";

    protected static final String SECOND_ADMIN_EMAIL = "secondAdmin@example.com";
    protected static final String SECOND_ADMIN_PASSWORD = "admin!second";

    protected static final String BLOCKED_ADMIN_EMAIL = "blockedAdmin@example.com";
    protected static final String BLOCKED_ADMIN_PASSWORD = "admin123!blocked";

    protected static final String USER_EMAIL = "user@example.com";
    protected static final String USER_PASSWORD = "Sanjay99!";

    protected static final String UNVERIFIED_USER_EMAIL = "unverifiedUser@example.com";
    protected static final String UNVERIFIED_USER_PASSWORD = "Sanjay99!unverified";

    protected static final String BLOCKED_USER_EMAIL = "blockedUser@example.com";
    protected static final String BLOCKED_USER_PASSWORD = "Sanjay99!blocked";

    private static boolean initialized = false;

    @Autowired
    @AclManaging
    protected UserService<AbstractUser<Long>,Long> aclUserService;

    @Autowired
    protected AbstractUserRepository<AbstractUser<Long>,Long> userRepository;

    @SpyBean
    protected MailSender<?> mailSender;
    @Autowired
    protected RapidSecurityContext<RapidAuthenticatedPrincipal> securityContext;
    @Autowired
    protected DataSource dataSource;
    @Autowired
    private WebApplicationContext context;

    protected MockMvc mvc;
    protected Map<Long, String> tokens = new HashMap<>(6);

    protected AbstractUser admin;
    protected AbstractUser secondAdmin;
    protected AbstractUser blockedAdmin;
    protected AbstractUser user;
    protected AbstractUser unverifiedUser;
    protected AbstractUser blockedUser;

    @BeforeEach
    public void baseSetUp() throws Exception {
        initMockMvc();
        if (!initialized) {
            clearTestData();
            createTestUsers();
            loginTestUsers();
        }
    }

    protected void initMockMvc(){
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    protected void clearTestData() throws SQLException {
        ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("test-data/removeAclInfo.sql"));
        userRepository.deleteAll();
    }

    protected void createTestUsers() throws BadEntityException {
        admin = aclUserService.save(createUser(ADMIN_EMAIL, ADMIN_PASSWORD, LemonRoles.ADMIN));
        secondAdmin = aclUserService.save(createUser(SECOND_ADMIN_EMAIL, SECOND_ADMIN_PASSWORD, LemonRoles.ADMIN));
        blockedAdmin = aclUserService.save(createUser(BLOCKED_ADMIN_EMAIL, BLOCKED_ADMIN_PASSWORD, LemonRoles.ADMIN,LemonRoles.BLOCKED));

        user = aclUserService.save(createUser(USER_EMAIL, USER_PASSWORD, LemonRoles.USER));
        unverifiedUser = aclUserService.save(createUser(UNVERIFIED_USER_EMAIL, UNVERIFIED_USER_PASSWORD, LemonRoles.USER));
        blockedUser = aclUserService.save(createUser(BLOCKED_USER_EMAIL, BLOCKED_USER_PASSWORD, LemonRoles.USER,LemonRoles.BLOCKED));
    }

    protected AbstractUser createUser(String email, String password, String... roles){
        return new User(email, password, roles);
    }

    protected void loginTestUsers() throws Exception {
        tokens.put(ADMIN_ID, login(ADMIN_EMAIL, ADMIN_PASSWORD));
        tokens.put(SECOND_ADMIN_ID, login(SECOND_ADMIN_EMAIL, SECOND_ADMIN_PASSWORD));
        tokens.put(BLOCKED_ADMIN_ID, login("blockedadmin@example.com", ADMIN_PASSWORD));
        tokens.put(USER_ID, login(USER_EMAIL, USER_PASSWORD));
        tokens.put(UNVERIFIED_USER_ID, login(UNVERIFIED_USER_EMAIL, USER_PASSWORD));
        tokens.put(BLOCKED_USER_ID, login("blockeduser@example.com", USER_PASSWORD));
    }

    protected String login(String userName, String password) throws Exception {
        MvcResult result = mvc.perform(post("/api/core/login")
                .param("username", userName)
                .param("password", password)
                .header("contentType", MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is(200))
                .andReturn();

        return result.getResponse().getHeader(HttpHeaders.AUTHORIZATION);
    }

    protected void ensureTokenWorks(String token) throws Exception {
        mvc.perform(get("/api/core/context")
                .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().is(200));
//                .andExpect(jsonPath("$.user.id").value(UNVERIFIED_USER_ID));
    }

//    protected void initAcl() throws SQLException {
//        if (!initialized) {
//            //only do this expensive stuff once -> permissions stay the same
//            ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("test-data/removeAclInfo.sql"));
////        User admin = userRepository.findById(ADMIN_ID).get();
////        Authentication adminAuth = new UsernamePasswordAuthenticationToken(admin.getName(), admin.getPassword()
////                , Lists.newArrayList(new SimpleGrantedAuthority(Role.ADMIN)));
//            securityContext.runAsAdmin(() -> {
//                try {
//                    giveAdminFullPermissionOver(USER_ID, UNVERIFIED_USER_ID, BLOCKED_USER_ID /*ADMIN_ID, SECOND_ADMIN_ID, BLOCKED_ADMIN_ID*/);
//                    giveFullPermissionAboutSelf(ADMIN_ID, SECOND_ADMIN_ID, BLOCKED_ADMIN_ID, USER_ID, UNVERIFIED_USER_ID, BLOCKED_USER_ID);
//                }catch (Exception e){
//                    throw new RuntimeException(e);
//                }
//            });
//            initialized = true;
//        }
//    }

//    protected void giveFullPermissionAboutSelf(Long... ids) throws BadEntityException {
//        for (Long id : ids) {
//            AbstractUser user = userService.findById(id).get();
//            permissionService.addPermissionForUserOver(user, BasePermission.ADMINISTRATION, user.getEmail());
//        }
//    }
//
//    protected void giveAdminFullPermissionOver(Long... ids) throws BadEntityException {
//        for (Long id : ids) {
//            AbstractUser user = userService.findById(id).get();
//            permissionService.addPermissionForAuthorityOver(user, BasePermission.ADMINISTRATION, RapidRoles.ADMIN);
//        }
//    }



}
