package com.github.vincemann.springrapid.syncdemo.controller.suite.template;

import com.github.vincemann.springrapid.coretest.controller.template.MvcControllerTestTemplate;
import com.github.vincemann.springrapid.syncdemo.dto.owner.CreateOwnerDto;
import com.github.vincemann.springrapid.syncdemo.dto.owner.ReadOwnerDto;
import org.springframework.stereotype.Component;
import com.github.vincemann.springrapid.syncdemo.controller.OwnerController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Component
public class OwnerControllerTestTemplate extends MvcControllerTestTemplate<OwnerController> {

    public ReadOwnerDto create2xx(CreateOwnerDto dto) throws Exception {
        return perform2xxAndDeserialize(post("/api/core/owner/create")
                .content(serialize(dto)), ReadOwnerDto.class);
    }

    public ReadOwnerDto

}
