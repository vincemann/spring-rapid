package com.github.vincemann.springrapid.coretest.controller.urlparamid;

import com.github.vincemann.springrapid.core.controller.GenericCrudController;
import com.github.vincemann.springrapid.core.security.RapidAuthenticatedPrincipal;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.coretest.controller.IntegrationControllerTest;
import com.github.vincemann.springrapid.coretest.controller.UrlParamIdCrudControllerTest;
import com.github.vincemann.springrapid.coretest.util.RapidTestUtil;
import lombok.Getter;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

@Getter
public class IntegrationUrlParamIdControllerTest
        <C extends GenericCrudController<?,Id,S,?,?>,
        Id extends Serializable,S extends CrudService<?,Id>>
            extends IntegrationControllerTest<C,Id>
                implements UrlParamIdCrudControllerTest<C,Id>
{
    private S service;
    private RapidSecurityContext<RapidAuthenticatedPrincipal> securityContext;

    @Autowired
    public void injectSecurityContext(RapidSecurityContext<RapidAuthenticatedPrincipal> securityContext) {
        this.securityContext = securityContext;
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
