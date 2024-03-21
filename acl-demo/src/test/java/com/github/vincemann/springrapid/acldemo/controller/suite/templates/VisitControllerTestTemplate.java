package com.github.vincemann.springrapid.acldemo.controller.suite.templates;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.vincemann.springrapid.acldemo.controller.VisitController;
import com.github.vincemann.springrapid.acldemo.dto.visit.CreateVisitDto;
import com.github.vincemann.springrapid.acldemo.dto.visit.ReadVisitDto;
import com.github.vincemann.springrapid.coretest.controller.template.MvcControllerTestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Component
public class VisitControllerTestTemplate extends MvcControllerTestTemplate<VisitController> {

    public MockHttpServletRequestBuilder create(CreateVisitDto dto) throws JsonProcessingException {
        return post("/api/core/visit/create")
                .content(serialize(dto))
                .contentType(MediaType.APPLICATION_JSON);
    }

    public MockHttpServletRequestBuilder find(long id){
        return get("/api/core/visit/find")
                .param("id",String.valueOf(id));
    }

    public ReadVisitDto find2xx(long id) throws Exception {
        return perform2xxAndDeserialize(find(id),ReadVisitDto.class);
    }

    public ReadVisitDto create2xx(CreateVisitDto dto, String token) throws Exception {
        return perform2xxAndDeserialize(create(dto).header(HttpHeaders.AUTHORIZATION,token),ReadVisitDto.class);
    }

    public MockHttpServletRequestBuilder addSpectator(long visitId, long spectatorId){
        return get("/api/core/visit/add-spectator")
                .param("visit",String.valueOf(visitId))
                .param("spectator",String.valueOf(spectatorId));
    }

    public MockHttpServletRequestBuilder removeSpectator(long visitId, long spectatorId){
        return get("/api/core/visit/remove-spectator")
                .param("visit",String.valueOf(visitId))
                .param("spectator",String.valueOf(spectatorId));
    }
}
