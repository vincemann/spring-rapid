package com.github.vincemann.springrapid.acldemo.controller.suite.templates;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.vincemann.springrapid.acldemo.controller.PetController;
import com.github.vincemann.springrapid.acldemo.dto.pet.CreatePetDto;
import com.github.vincemann.springrapid.acldemo.dto.pet.OwnerReadsOwnPetDto;
import com.github.vincemann.springrapid.acldemo.dto.pet.OwnerUpdatesPetDto;
import com.github.vincemann.springrapid.acldemo.dto.pet.UpdateIllnessDto;
import com.github.vincemann.springrapid.coretest.controller.template.MvcControllerTestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@Component
public class PetControllerTestTemplate extends MvcControllerTestTemplate<PetController> {

    public MockHttpServletRequestBuilder create(CreatePetDto dto) throws JsonProcessingException {
        return post("/api/core/pet/create")
                .content(toJson(dto))
                .contentType(MediaType.APPLICATION_JSON_UTF8);
    }

    public MockHttpServletRequestBuilder find(String name){
        return get("/api/core/pet/find")
                .param("name",name)
                .contentType(MediaType.APPLICATION_JSON_UTF8);
    }



    public MockHttpServletRequestBuilder ownerUpdatesPet(OwnerUpdatesPetDto dto) throws JsonProcessingException {
        return put("/api/core/pet/owner-update")
                .content(toJson(dto))
                .contentType(MediaType.APPLICATION_JSON_UTF8);
    }

    public MockHttpServletRequestBuilder addIllness(UpdateIllnessDto dto) throws JsonProcessingException {
        return put("/api/core/pet/add-illness")
                .content(toJson(dto))
                .contentType(MediaType.APPLICATION_JSON_UTF8);
    }

    public MockHttpServletRequestBuilder removeIllness(UpdateIllnessDto dto) throws JsonProcessingException {
        return put("/api/core/pet/remove-illness")
                .content(toJson(dto))
                .contentType(MediaType.APPLICATION_JSON_UTF8);
    }

    public OwnerReadsOwnPetDto ownerUpdatesPet2xx(OwnerUpdatesPetDto dto, String token) throws Exception {
        return perform2xxAndDeserialize(ownerUpdatesPet(dto)
                        .header(HttpHeaders.AUTHORIZATION,token),
                OwnerReadsOwnPetDto.class);
    }


    public OwnerReadsOwnPetDto create2xx(CreatePetDto dto, String token) throws Exception {
        return perform2xxAndDeserialize(create(dto)
                .header(HttpHeaders.AUTHORIZATION,token),
                OwnerReadsOwnPetDto.class);
    }

}
