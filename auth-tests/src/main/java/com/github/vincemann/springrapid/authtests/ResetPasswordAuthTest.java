package com.github.vincemann.springrapid.authtests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.vincemann.springrapid.auth.domain.dto.ResetPasswordForm;
import com.github.vincemann.springrapid.auth.service.AbstractUserService;
import com.github.vincemann.springrapid.auth.service.token.JweTokenService;
import com.github.vincemann.springrapid.auth.util.RapidJwt;
import com.github.vincemann.springrapid.core.util.JsonUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ResetPasswordAuthTest extends AbstractRapidAuthTest {

    private String forgotPasswordCode;

    @Autowired
    private JweTokenService jweTokenService;

    @BeforeEach
    public void setup() throws Exception {
        super.setup();
        forgotPasswordCode = jweTokenService.createToken(
                RapidJwt.create(AbstractUserService.FORGOT_PASSWORD_AUDIENCE, ADMIN_EMAIL, 60000L)
        );
    }

    @Test
    public void testResetPassword() throws Exception {

        final String NEW_PASSWORD = "newPassword!";

        //Thread.sleep(1001L);

        mvc.perform(post(authProperties.getController().getResetPasswordUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .content(form(forgotPasswordCode, NEW_PASSWORD)))
                .andExpect(status().is(200))
                .andExpect(header().string(HttpHeaders.AUTHORIZATION, containsString(".")))
                .andExpect(jsonPath("$.id").value(getAdmin().getId()));

        // New password should work
        login(ADMIN_EMAIL, NEW_PASSWORD);

        // Repeating shouldn't work
        mvc.perform(post(authProperties.getController().getResetPasswordUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .content(form(forgotPasswordCode, NEW_PASSWORD)))
                .andExpect(status().is(403));
    }

    @Test
    public void testResetPasswordInvalidData() throws Exception {

        // Wrong code
        mvc.perform(post(authProperties.getController().getResetPasswordUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .content(form("wrong-code", "abc99!")))
                .andExpect(status().is(401));

        // Blank password
        mvc.perform(post(authProperties.getController().getResetPasswordUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .content(form(forgotPasswordCode, "")))
                .andExpect(status().is(422));

        // Invalid password
        mvc.perform(post(authProperties.getController().getResetPasswordUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .content(form(forgotPasswordCode, "abc")))
                .andExpect(status().is(422));
    }

    private String form(String code, String newPassword) throws JsonProcessingException {

        ResetPasswordForm form = new ResetPasswordForm();
        form.setCode(code);
        form.setNewPassword(newPassword);

        return JsonUtils.toJson(form);
    }
}
