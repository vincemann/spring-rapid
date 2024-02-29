package com.github.vincemann.springrapid.acldemo.controller.suite.templates;

import com.github.vincemann.springrapid.acldemo.controller.OwnerController;
import com.github.vincemann.springrapid.acldemo.dto.owner.ReadOwnOwnerDto;
import com.github.vincemann.springrapid.acldemo.dto.owner.SignupOwnerDto;
import com.github.vincemann.springrapid.coretest.controller.template.CrudControllerTestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Component
public class OwnerControllerTestTemplate  extends CrudControllerTestTemplate<OwnerController> {

    public ReadOwnOwnerDto signup(SignupOwnerDto dto) throws Exception {
        String json = getMvc().perform(post(getController().getSignupUrl())
                        .content(getController().getJsonMapper().writeDto(dto)))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString();
        return getController().getJsonMapper().readDto(json,ReadOwnOwnerDto.class);
    }
}
