package com.github.vincemann.springrapid.authdemo;

import com.github.vincemann.springrapid.authdemo.dto.ReadUserDto;
import com.github.vincemann.springrapid.authdemo.dto.SignupDto;
import com.github.vincemann.springrapid.authdemo.suite.MyUserControllerTestTemplate;
import com.github.vincemann.springrapid.authtests.AuthIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.github.vincemann.springrapid.authtests.AuthTestAdapter.USER_PASSWORD;

public class SignupTest extends AuthIntegrationTest {

    @Autowired
    MyUserControllerTestTemplate userController;

    @Test
    public void signup() throws Exception {
        // when
        SignupDto dto = SignupDto.builder()
                .name("signupuser")
                .contactInformation("newEmail@mail.com")
                .password(USER_PASSWORD)
                .build();
        ReadUserDto response = userController.perform2xxAndDeserialize(
                userController.signup(dto), ReadUserDto.class);
        // then
        Assertions.assertEquals("newEmail@mail.com",response.getContactInformation());
        userController.verifyMsgWasSent("newEmail@mail.com");
    }

}
