package com.github.vincemann.springrapid.authtests.tests;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.sec.bruteforce.LoginAttemptService;
import com.github.vincemann.springrapid.authtests.AuthIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.github.vincemann.springrapid.authtests.AuthTestAdapter.*;

@SpringBootTest({
        "rapid-auth.bruteforce-protection=true",
        "rapid-auth.max-login-attempts=3",
        "rapid-auth.create-admins=false"
})
public class LoginBruteForceTest extends AuthIntegrationTest {

    private static final int MAX_LOGIN_TRIES = 3;

    @Autowired
    LoginAttemptService loginAttemptService;

    @Test
    public void givenUserTriedToLoginTooOften_whenTryingAgain_thenTooManyRequestError() throws Exception {
        AbstractUser<?> user = testAdapter.createUser();
        for (int i =0 ;i<MAX_LOGIN_TRIES;i++){
            mvc.perform(userController.login(USER_CONTACT_INFORMATION,WRONG_PASSWORD+i))
                    .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));
        }
        mvc.perform(userController.login(USER_CONTACT_INFORMATION,WRONG_PASSWORD))
                .andExpect(status().is(HttpStatus.TOO_MANY_REQUESTS.value()));

    }

    @Test
    public void givenUserTriedToLoginALotButIsNotBlockedYet_whenLoggingInSuccessfully_thenAmountTriesResetForUser() throws Exception {
        AbstractUser<?> user = testAdapter.createUser();
        for (int i =0 ;i<MAX_LOGIN_TRIES-1;i++){
            mvc.perform(userController.login(USER_CONTACT_INFORMATION,WRONG_PASSWORD+i))
                    .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));
        }
        mvc.perform(userController.login(USER_CONTACT_INFORMATION,USER_PASSWORD))
                .andExpect(status().is(200));
        for (int i =0 ;i<MAX_LOGIN_TRIES-1;i++){
            mvc.perform(userController.login(USER_CONTACT_INFORMATION,WRONG_PASSWORD+i))
                    .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));
        }
    }


    @AfterEach
    void resetLimit() {
        loginAttemptService.reset();
    }
}
