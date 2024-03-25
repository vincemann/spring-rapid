package com.github.vincemann.springrapid.syncdemo.controller.suite.template;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.vincemann.springrapid.coretest.controller.template.MvcControllerTestTemplate;
import com.github.vincemann.springrapid.syncdemo.controller.PetController;
import com.github.vincemann.springrapid.syncdemo.dto.pet.CreatePetDto;
import com.github.vincemann.springrapid.syncdemo.dto.pet.ReadPetDto;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Component
public class PetControllerTestTemplate extends MvcControllerTestTemplate<PetController> {

    public ReadPetDto create2xx(CreatePetDto dto) throws Exception {
        return perform2xxAndDeserialize(create(dto),
                ReadPetDto.class);
    }

    public MockHttpServletRequestBuilder create(CreatePetDto dto) throws JsonProcessingException {
        return post("/api/core/pet/create")
                .content(toJson(dto))
                .contentType(MediaType.APPLICATION_JSON_UTF8);
    }

    public List<ReadPetDto> findSome2xx(List<Long> ids) throws Exception {
        return perform2xxAndDeserializeToList(get("/api/core/pet/find-some")
                        .content(toJson(ids))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                , ReadPetDto.class);
    }
}
