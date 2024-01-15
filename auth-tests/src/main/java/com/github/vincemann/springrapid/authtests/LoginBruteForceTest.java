package com.github.vincemann.springrapid.authtests;

import com.github.vincemann.springrapid.auth.security.bruteforce.LoginAttemptService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.github.vincemann.springrapid.authtests.adapter.AuthTestAdapter.*;

@SpringBootTest({
        "lemon.recaptcha.sitekey=",
        "rapid-auth.loginBruteforceProtection=true",
        "rapid-auth.maxLoginAttempts=5"
})
public class LoginBruteForceTest extends RapidAuthIntegrationTest {

    private static final int MAX_LOGIN_TRIES = 5;

    @Autowired
    LoginAttemptService loginAttemptService;

    @Test
    public void tooManyLoginTries_tooManyRequestsResponse() throws Exception {
        String wrongPassword = "bruteWrongPw";
        for (int i =0 ;i<MAX_LOGIN_TRIES;i++){
            mvc.perform(userController.login(ADMIN_CONTACT_INFORMATION,wrongPassword+i))
                    .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));
        }
        mvc.perform(userController.login(ADMIN_CONTACT_INFORMATION,wrongPassword))
                .andExpect(status().is(HttpStatus.TOO_MANY_REQUESTS.value()));

    }

    @Test
    public void maxLoginTries_thenCorrectLogin_resetsEverything() throws Exception {
        String wrongPassword = "bruteWrongPw";
        for (int i =0 ;i<MAX_LOGIN_TRIES-1;i++){
            mvc.perform(userController.login(ADMIN_CONTACT_INFORMATION,wrongPassword+i))
                    .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));
        }
        mvc.perform(userController.login(ADMIN_CONTACT_INFORMATION,ADMIN_PASSWORD))
                .andExpect(status().is(200));
        for (int i =0 ;i<MAX_LOGIN_TRIES-1;i++){
            mvc.perform(userController.login(ADMIN_CONTACT_INFORMATION,wrongPassword+i))
                    .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));
        }

    }


    @AfterEach
    void resetLimit() throws java.sql.SQLException {
        loginAttemptService.reset();
    }
}
