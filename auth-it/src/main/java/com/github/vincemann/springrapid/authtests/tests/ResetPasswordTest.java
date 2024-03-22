package com.github.vincemann.springrapid.authtests.tests;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.msg.AuthMessage;
import com.github.vincemann.springrapid.auth.dto.ResetPasswordDto;
import com.github.vincemann.springrapid.authtests.AuthIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static com.github.vincemann.springrapid.authtests.AuthTestAdapter.*;


public class ResetPasswordTest extends AuthIntegrationTest {


    private ResetPasswordDto resetPasswordDto(String newPassword, String code) {
        return new ResetPasswordDto(newPassword,code);
    }

    @Test
    public void givenForgotPasswordAndClickedOnCodeInMsg_thenGetDirectedToForgotPasswordPage() throws Exception {
        AbstractUser<?> user = testAdapter.createUser();
        userController.forgotPassword2xx(USER_CONTACT_INFORMATION);
        AuthMessage msg = verifyMsgWasSent(user.getContactInformation());
        String code = msg.getCode();
        String html = mvc.perform(userController.getResetPasswordView(msg.getLink()))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString();
        Assertions.assertTrue(html.contains("Reset Password"));
    }

    @Test
    public void userCanResetPasswordWithCorrectCode() throws Exception {
        AbstractUser<?> user = testAdapter.createUser();
        userController.forgotPassword2xx(USER_CONTACT_INFORMATION);
        AuthMessage msg = verifyMsgWasSent(user.getContactInformation());
        mvc.perform(userController.resetPassword(resetPasswordDto(NEW_PASSWORD,msg.getCode())))
                .andExpect(status().is2xxSuccessful())
                .andExpect(header().string(HttpHeaders.AUTHORIZATION, containsString(".")))
                        .andExpect(content().string(""));

        // New password should work
        userController.login2xx(USER_CONTACT_INFORMATION, NEW_PASSWORD);
    }

    @Test
    public void cantResetPasswordWithSameCodeTwice() throws Exception {
        AbstractUser<?> user = testAdapter.createUser();
        userController.forgotPassword2xx(USER_CONTACT_INFORMATION);
        AuthMessage msg = verifyMsgWasSent(user.getContactInformation());
        mvc.perform(userController.resetPassword(resetPasswordDto(NEW_PASSWORD,msg.getCode())))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(""));

        // New password should work
        userController.login2xx(USER_CONTACT_INFORMATION, NEW_PASSWORD);

        // Repeating shouldn't work
        mvc.perform(userController.resetPassword(resetPasswordDto(USER_PASSWORD,msg.getCode())))
                .andExpect(status().isForbidden());

        userController.login2xx(USER_CONTACT_INFORMATION, NEW_PASSWORD);
    }

    @Test
    public void cantResetPasswordWithInvalidCode() throws Exception {
        AbstractUser<?> user = testAdapter.createUser();
        userController.forgotPassword2xx(USER_CONTACT_INFORMATION);
        AuthMessage msg = verifyMsgWasSent(user.getContactInformation());
        String code = msg.getCode();
        String invalidCode = code +"invalid";
        mvc.perform(userController.resetPassword(resetPasswordDto(NEW_PASSWORD,invalidCode)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void cantResetPasswordWithInvalidNewPassword() throws Exception {
        AbstractUser<?> user = testAdapter.createUser();
        // Blank password
        userController.forgotPassword2xx(USER_CONTACT_INFORMATION);
        AuthMessage msg = verifyMsgWasSent(user.getContactInformation());
        mvc.perform(userController.resetPassword(resetPasswordDto("",msg.getCode())))
                .andExpect(status().isBadRequest());


        // Invalid password
        userController.forgotPassword2xx(USER_CONTACT_INFORMATION);
        msg = verifyMsgWasSent(user.getContactInformation());
        mvc.perform(userController.resetPassword(resetPasswordDto(INVALID_PASSWORD,msg.getCode())))
                .andExpect(status().isBadRequest());
    }




}
