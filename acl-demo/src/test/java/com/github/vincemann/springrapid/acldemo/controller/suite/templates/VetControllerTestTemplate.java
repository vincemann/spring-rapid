package com.github.vincemann.springrapid.acldemo.controller.suite.templates;

import com.github.vincemann.springrapid.acldemo.controller.VetController;
import com.github.vincemann.springrapid.acldemo.dto.vet.ReadVetDto;
import com.github.vincemann.springrapid.acldemo.dto.vet.SignupVetDto;
import com.github.vincemann.springrapid.authtest.MvcControllerTestTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Component
public class VetControllerTestTemplate extends MvcControllerTestTemplate<VetController> {

    public ReadVetDto signup(SignupVetDto dto) throws Exception {
        String json = getMvc().perform(post("/api/core/vet/signup")
                        .content(serialize(dto))
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString();
        return getObjectMapper().readValue(json,ReadVetDto.class);
    }
}

