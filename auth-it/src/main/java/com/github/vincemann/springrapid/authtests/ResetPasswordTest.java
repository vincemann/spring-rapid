package com.github.vincemann.springrapid.authtests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.vincemann.springrapid.auth.dto.ResetPasswordDto;
import com.github.vincemann.springrapid.auth.dto.ResetPasswordView;
import com.github.vincemann.springrapid.auth.mail.MailData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static com.github.vincemann.springrapid.authtests.adapter.AuthTestAdapter.*;


public class ResetPasswordTest extends RapidAuthIntegrationTest {


    private ResetPasswordDto resetPasswordDto(String newPassword, String code) throws JsonProcessingException {
        return new ResetPasswordDto(newPassword,code);
    }

    @Test
    public void getDirectedToForgotPasswordPage() throws Exception {
        MailData mailData = userController.forgotPassword2xx(USER_CONTACT_INFORMATION);
        String code = mailData.getCode();
        String html = mvc.perform(userController.getResetPasswordView(mailData.getLink()))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString();
        Assertions.assertTrue(html.contains("Reset Password"));
    }

    @Test
    public void canResetPasswordWithCorrectCode() throws Exception {
        MailData mailData = userController.forgotPassword2xx(USER_CONTACT_INFORMATION);
        mvc.perform(userController.resetPassword(resetPasswordDto(NEW_PASSWORD,mailData.getCode())))
                .andExpect(status().is2xxSuccessful())
                .andExpect(header().string(HttpHeaders.AUTHORIZATION, containsString(".")))
                        .andExpect(content().string(""));

        // New password should work
        login2xx(USER_CONTACT_INFORMATION, NEW_PASSWORD);
    }

    @Test
    public void cantResetPasswordWithSameCodeTwice() throws Exception {
        MailData mailData = userController.forgotPassword2xx(USER_CONTACT_INFORMATION);
        mvc.perform(userController.resetPassword(resetPasswordDto(NEW_PASSWORD,mailData.getCode())))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(""));

        // New password should work
        login2xx(USER_CONTACT_INFORMATION, NEW_PASSWORD);

        // Repeating shouldn't work
        mvc.perform(userController.resetPassword(resetPasswordDto(USER_PASSWORD,mailData.getCode())))
                .andExpect(status().isForbidden());

        login2xx(USER_CONTACT_INFORMATION, NEW_PASSWORD);
    }

    @Test
    public void cantResetPasswordWithInvalidCode() throws Exception {
        MailData mailData = userController.forgotPassword2xx(USER_CONTACT_INFORMATION);
        String code = mailData.getCode();
        String invalidCode = code +"invalid";
        mvc.perform(userController.resetPassword(resetPasswordDto(NEW_PASSWORD,invalidCode)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void cantResetPasswordWithInvalidPassword() throws Exception {
        // Blank password
        MailData mailData = userController.forgotPassword2xx(USER_CONTACT_INFORMATION);
        mvc.perform(userController.resetPassword(resetPasswordDto("",mailData.getCode())))
                .andExpect(status().isBadRequest());


        // Invalid password
        mailData = userController.forgotPassword2xx(USER_CONTACT_INFORMATION);
        mvc.perform(userController.resetPassword(resetPasswordDto(INVALID_PASSWORD,mailData.getCode())))
                .andExpect(status().isBadRequest());
    }




}
