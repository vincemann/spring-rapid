package com.github.vincemann.springrapid.authtest;

import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.domain.AbstractUser;
import com.github.vincemann.springrapid.coretest.login.MockLoginTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Need to call {@link #setMvc(MockMvc)} before using.
 **/
public class AuthITLoginTemplate {

    private MockMvc mvc;
    private AuthProperties authProperties;

    public ResultActions login(AbstractUser user){
        return login(new LoginForm(user.getEmail(),user.getPassword()));
    }

    public ResultActions login(LoginForm principal) {
        try {
            return mvc.perform(post(authProperties.getController().getLoginUrl())
                    .param("username", principal.getEmail())
                    .param("password", principal.getPassword())
                    .header("contentType", MediaType.APPLICATION_FORM_URLENCODED));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Autowired
    public void injectAuthProperties(AuthProperties authProperties) {
        this.authProperties = authProperties;
    }

    public void setMvc(MockMvc mvc) {
        this.mvc = mvc;
    }
}
