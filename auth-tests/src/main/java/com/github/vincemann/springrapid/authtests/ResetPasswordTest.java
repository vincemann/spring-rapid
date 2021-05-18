package com.github.vincemann.springrapid.authtests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.vincemann.springrapid.auth.domain.dto.ResetPasswordDto;
import com.github.vincemann.springrapid.auth.mail.MailData;
import com.github.vincemann.springrapid.auth.service.AbstractUserService;
import com.github.vincemann.springrapid.auth.service.token.JweTokenService;
import com.github.vincemann.springrapid.auth.util.RapidJwt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static com.github.vincemann.springrapid.authtests.adapter.AuthTestAdapter.*;


public class ResetPasswordTest extends AbstractRapidAuthIntegrationTest {

    // todo maybe add mod token tests like in changeEmailTests
//    @Autowired
//    private JweTokenService jweTokenService;

    private ResetPasswordDto resetPasswordDto(String code, String newPassword) throws JsonProcessingException {
        ResetPasswordDto dto = new ResetPasswordDto();
        dto.setCode(code);
        dto.setNewPassword(newPassword);
        return dto;
    }

    @Test
    public void canResetPasswordWithCorrectCode() throws Exception {
        MailData mailData = testTemplate.forgotPassword2xx(USER_EMAIL);
        String code = mailData.getCode();
        testTemplate.resetPassword(resetPasswordDto(code,NEW_PASSWORD))
                .andExpect(status().is2xxSuccessful())
                .andExpect(header().string(HttpHeaders.AUTHORIZATION, containsString(".")))
                .andExpect(jsonPath("$.id").value(getUser().getId()));

        // New password should work
        login2xx(USER_EMAIL, NEW_PASSWORD);
    }

    @Test
    public void cantResetPasswordWithSameCodeTwice() throws Exception {
        MailData mailData = testTemplate.forgotPassword2xx(USER_EMAIL);
        String code = mailData.getCode();
        testTemplate.resetPassword(resetPasswordDto(code,NEW_PASSWORD))
                .andExpect(status().is2xxSuccessful());

        // New password should work
        login2xx(USER_EMAIL, NEW_PASSWORD);

        // Repeating shouldn't work
        testTemplate.resetPassword(resetPasswordDto(code,USER_PASSWORD))
                .andExpect(status().isForbidden());

        login2xx(USER_EMAIL, NEW_PASSWORD);
    }

    @Test
    public void cantResetPasswordWithInvalidCode() throws Exception {
        MailData mailData = testTemplate.forgotPassword2xx(USER_EMAIL);
        String code = mailData.getCode();
        String invalidCode = code +"invalid";
        testTemplate.resetPassword(resetPasswordDto(invalidCode,NEW_PASSWORD))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void cantResetPasswordWithInvalidPassword() throws Exception {
        // Blank password
        MailData mailData = testTemplate.forgotPassword2xx(USER_EMAIL);
        String code = mailData.getCode();
        testTemplate.resetPassword(resetPasswordDto(code,""))
                .andExpect(status().isBadRequest());


        // Invalid password
        mailData = testTemplate.forgotPassword2xx(USER_EMAIL);
        code = mailData.getCode();
        testTemplate.resetPassword(resetPasswordDto(code,INVALID_PASSWORD))
                .andExpect(status().isBadRequest());
    }




}
