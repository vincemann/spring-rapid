package com.github.vincemann.springrapid.acldemo.controller.suite.templates;

import com.github.vincemann.springrapid.acldemo.controller.OwnerController;
import com.github.vincemann.springrapid.acldemo.dto.owner.OwnerReadsOwnOwnerDto;
import com.github.vincemann.springrapid.acldemo.dto.owner.SignupOwnerDto;
import com.github.vincemann.springrapid.coretest.controller.template.CrudControllerTestTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Component
public class OwnerControllerTestTemplate extends CrudControllerTestTemplate<OwnerController> {

    public OwnerReadsOwnOwnerDto signup(SignupOwnerDto dto) throws Exception {
        String json = getMvc().perform(post(getController().getSignupUrl())
                        .content(serialize(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString();
        return getController().getObjectMapper().readValue(json, OwnerReadsOwnOwnerDto.class);
    }


    public void addPetsSpectator(long permittedOwner, long targetOwner, String token) throws Exception {
        getMvc().perform(withToken(get(getController().getAddPetSpectatorUrl())
                .param("permitted",String.valueOf(permittedOwner))
                .param("target",String.valueOf(targetOwner)),
                        token))
                .andExpect(status().is2xxSuccessful());
    }
}
