package com.github.vincemann.springrapid.authdemo.suite;

import com.github.vincemann.springrapid.authdemo.controller.UserController;
import com.github.vincemann.springrapid.authdemo.dto.SignupDto;
import com.github.vincemann.springrapid.authtest.AbstractUserControllerTestTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class UserControllerTestTemplate extends AbstractUserControllerTestTemplate<UserController> {

    public MockHttpServletRequestBuilder signup(SignupDto dto) throws Exception {
        return post("/api/core/user/signup")
                .content(toJson(dto))
                .contentType(MediaType.APPLICATION_JSON_UTF8);
    }
}
