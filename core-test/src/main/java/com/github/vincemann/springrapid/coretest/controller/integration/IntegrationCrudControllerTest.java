package com.github.vincemann.springrapid.coretest.controller.integration;

import com.github.vincemann.springrapid.core.controller.GenericCrudController;
import com.github.vincemann.springrapid.core.security.RapidAuthenticatedPrincipal;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.coretest.controller.template.CrudControllerTestTemplate;
import com.github.vincemann.springrapid.coretest.util.TransactionalRapidTestUtil;
import lombok.Getter;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
public class IntegrationCrudControllerTest
        <C extends GenericCrudController<?,?,S,?,?>, S extends CrudService<?,?>>
            extends AbstractIntegrationControllerTest<C,CrudControllerTestTemplate>
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

    // use sql scripts for clean up
//    @AfterEach
//    void cleanup(){
//        TransactionalRapidTestUtil.clear(service);
//    }
}
