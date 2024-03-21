package com.github.vincemann.springrapid.syncdemo.controller.suite.template;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.vincemann.springrapid.coretest.controller.template.MvcControllerTestTemplate;
import com.github.vincemann.springrapid.syncdemo.dto.owner.CreateOwnerDto;
import com.github.vincemann.springrapid.syncdemo.dto.owner.ReadOwnerDto;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import com.github.vincemann.springrapid.syncdemo.controller.OwnerController;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Component
public class OwnerControllerTestTemplate extends MvcControllerTestTemplate<OwnerController> {

    public ReadOwnerDto create2xx(CreateOwnerDto dto) throws Exception {
        return perform2xxAndDeserialize(post("/api/core/owner/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(serialize(dto)), ReadOwnerDto.class);
    }

    public ReadOwnerDto find2xx(long id) throws Exception {
        return perform2xxAndDeserialize(get("/api/core/owner/find")
                .param("id", String.valueOf(id)), ReadOwnerDto.class);
    }

    public List<ReadOwnerDto> findSome2xx(List<Long> ids) throws Exception {
        return perform2xxAndDeserializeToList(get("/api/core/owner/find-some")
                        .content(serialize(ids))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                , ReadOwnerDto.class);
    }

}
