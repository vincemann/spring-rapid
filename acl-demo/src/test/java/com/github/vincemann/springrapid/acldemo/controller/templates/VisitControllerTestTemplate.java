package com.github.vincemann.springrapid.acldemo.controller.templates;

import com.github.vincemann.springrapid.acldemo.controller.VisitController;
import com.github.vincemann.springrapid.coretest.controller.template.CrudControllerTestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@Component
public class VisitControllerTestTemplate extends CrudControllerTestTemplate<VisitController> {

    public MockHttpServletRequestBuilder subscribe(String token, Long ownerId, Long visitId, boolean subscribe){
        return get(getController().getSubscribeOwnerUrl())
                .header(HttpHeaders.AUTHORIZATION,token)
                .param("owner-id",ownerId.toString())
                .param("visit-id",visitId.toString())
                .param("subscribe",String.valueOf(subscribe));
    }
}
