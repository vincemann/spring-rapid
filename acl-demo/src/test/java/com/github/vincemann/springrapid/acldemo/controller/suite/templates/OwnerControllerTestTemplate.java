package com.github.vincemann.springrapid.acldemo.controller.suite.templates;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.vincemann.springrapid.acldemo.owner.OwnerController;
import com.github.vincemann.springrapid.acldemo.owner.dto.OwnerReadsOwnOwnerDto;
import com.github.vincemann.springrapid.acldemo.owner.dto.SignupOwnerDto;
import com.github.vincemann.springrapid.acldemo.owner.dto.UpdateOwnerDto;
import com.github.vincemann.springrapid.authtest.MvcControllerTestTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Component
public class OwnerControllerTestTemplate extends MvcControllerTestTemplate<OwnerController> {

    public OwnerReadsOwnOwnerDto signup(SignupOwnerDto dto) throws Exception {
        String json = getMvc().perform(post("/api/core/owner/signup")
                        .content(serialize(dto))
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString();
        return getObjectMapper().readValue(json, OwnerReadsOwnOwnerDto.class);
    }


    public MockHttpServletRequestBuilder update(UpdateOwnerDto dto) throws JsonProcessingException {
        return put("/api/core/owner/update")
                .content(serialize(dto))
                .contentType(MediaType.APPLICATION_JSON_UTF8);
    }


    public void addPetsSpectator(long permittedOwner, long targetOwner, String token) throws Exception {
        getMvc().perform(withToken(put("/api/core/owner/add-pet-spectator")
                .param("permitted",String.valueOf(permittedOwner))
                .param("target",String.valueOf(targetOwner)),
                        token))
                .andExpect(status().is2xxSuccessful());
    }
}
