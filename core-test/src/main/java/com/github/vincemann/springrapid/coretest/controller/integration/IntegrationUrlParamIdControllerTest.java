package com.github.vincemann.springrapid.coretest.controller.integration;

import com.github.vincemann.springrapid.core.controller.GenericCrudController;
import com.github.vincemann.springrapid.core.security.RapidAuthenticatedPrincipal;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.coretest.controller.template.UrlParamIdCrudControllerTestTemplate;
import com.github.vincemann.springrapid.coretest.util.RapidTestUtil;
import lombok.Getter;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

@Getter
public class IntegrationUrlParamIdControllerTest
        <C extends GenericCrudController<?,Id,S,?,?>,
        Id extends Serializable,S extends CrudService<?,Id>>
            extends IntegrationControllerTest<C,Id,UrlParamIdCrudControllerTestTemplate<C,Id>>
{
    private S service;
    private RapidSecurityContext<RapidAuthenticatedPrincipal> securityContext;

    @Autowired
    public void injectSecurityContext(RapidSecurityContext<RapidAuthenticatedPrincipal> securityContext) {
        this.securityContext = securityContext;
    }

    @Override
    public UrlParamIdCrudControllerTestTemplate<C, Id> createTestTemplate() {
        return new UrlParamIdCrudControllerTestTemplate<>(getController());
    }

    @Autowired
    public void injectService(S service) {
        this.service = service;
    }

    @AfterEach
    void cleanup(){
        RapidTestUtil.clear(service);
    }


}
