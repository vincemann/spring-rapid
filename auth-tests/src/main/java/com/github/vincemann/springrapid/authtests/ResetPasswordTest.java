package com.github.vincemann.springrapid.authtests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.vincemann.springrapid.auth.domain.dto.ResetPasswordDto;
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

public class ResetPasswordTest extends AbstractRapidAuthIntegrationTest {

    final String NEW_PASSWORD = "newPassword123!";

    private String adminForgotPasswordCode;

    @Autowired
    private JweTokenService jweTokenService;

    private String resetPasswordDto(String code, String newPassword) throws JsonProcessingException {
        ResetPasswordDto dto = new ResetPasswordDto();
        dto.setCode(code);
        dto.setNewPassword(newPassword);
        return serialize(dto);
    }

    @BeforeEach
    public void setup() throws Exception {
        super.setup();
        adminForgotPasswordCode = jweTokenService.createToken(
                RapidJwt.create(AbstractUserService.FORGOT_PASSWORD_SUBJECT, ADMIN_EMAIL, 60000L)
        );
    }

    @Test
    public void canResetPasswordWithCorrectCode() throws Exception {

        //Thread.sleep(1001L);

        mvc.perform(post(authProperties.getController().getResetPasswordUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .content(resetPasswordDto(adminForgotPasswordCode, NEW_PASSWORD)))
                .andExpect(status().is(200))
                .andExpect(header().string(HttpHeaders.AUTHORIZATION, containsString(".")))
                .andExpect(jsonPath("$.id").value(getAdmin().getId()));

        // New password should work
        login2xx(ADMIN_EMAIL, NEW_PASSWORD);

        // Repeating shouldn't work
        mvc.perform(post(authProperties.getController().getResetPasswordUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .content(resetPasswordDto(adminForgotPasswordCode, NEW_PASSWORD)))
                .andExpect(status().is(403));
    }

    @Test
    public void cantResetPasswordWithInvalidCode_orInvalidNewPassword() throws Exception {

        // Wrong code
        mvc.perform(post(authProperties.getController().getResetPasswordUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .content(resetPasswordDto("wrong-code", NEW_PASSWORD)))
                .andExpect(status().is(400));

        // Blank password
        mvc.perform(post(authProperties.getController().getResetPasswordUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .content(resetPasswordDto(adminForgotPasswordCode, "")))
                .andExpect(status().is(400));

        // Invalid password
        mvc.perform(post(authProperties.getController().getResetPasswordUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .content(resetPasswordDto(adminForgotPasswordCode, "abc")))
                .andExpect(status().is(400));
    }


}
