package com.github.vincemann.springrapid.authtests;

import com.github.vincemann.springrapid.auth.sec.bruteforce.LoginAttemptService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.github.vincemann.springrapid.authtests.adapter.AuthTestAdapter.*;

@SpringBootTest({
        "rapid-auth.bruteforce-protection=true",
        "rapid-auth.max-login-attempts=5",
        "rapid-auth.create-admins=false"
})
public class LoginBruteForceTest extends RapidAuthIntegrationTest {

    private static final int MAX_LOGIN_TRIES = 5;

    @Autowired
    LoginAttemptService loginAttemptService;

    @Test
    public void givenUserTriedToLoginTooOften_whenTryingAgain_thenTooManyRequestError() throws Exception {
        String wrongPassword = "bruteWrongPw";
        for (int i =0 ;i<MAX_LOGIN_TRIES;i++){
            mvc.perform(userController.login(ADMIN_CONTACT_INFORMATION,wrongPassword+i))
                    .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));
        }
        mvc.perform(userController.login(ADMIN_CONTACT_INFORMATION,wrongPassword))
                .andExpect(status().is(HttpStatus.TOO_MANY_REQUESTS.value()));

    }

    @Test
    public void givenUserTriedToLoginALotButIsNotBlockedYet_whenLoggingInSuccessfully_thenAmountTriesResetForUser() throws Exception {
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
    void resetLimit() {
        loginAttemptService.reset();
    }
}
