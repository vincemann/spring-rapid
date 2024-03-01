package com.github.vincemann.springrapid.acldemo.controller.suite.templates;

import com.github.vincemann.springrapid.acldemo.controller.VetController;
import com.github.vincemann.springrapid.acldemo.dto.owner.ReadOwnOwnerDto;
import com.github.vincemann.springrapid.acldemo.dto.vet.ReadVetDto;
import com.github.vincemann.springrapid.acldemo.dto.vet.SignupVetDto;
import com.github.vincemann.springrapid.coretest.controller.template.CrudControllerTestTemplate;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.stereotype.Component;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestComponent
public class VetControllerTestTemplate extends CrudControllerTestTemplate<VetController> {

    public ReadVetDto signup(SignupVetDto dto) throws Exception {
        String json = getMvc().perform(post(getController().getSignupUrl())
                        .content(getController().getJsonMapper().writeDto(dto)))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString();
        return getController().getJsonMapper().readDto(json,ReadVetDto.class);
    }
}

