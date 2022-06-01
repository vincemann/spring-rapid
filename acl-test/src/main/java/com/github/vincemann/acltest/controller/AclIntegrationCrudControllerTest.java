package com.github.vincemann.acltest.controller;

import com.github.vincemann.springrapid.core.controller.GenericCrudController;
import com.github.vincemann.springrapid.core.security.RapidAuthenticatedPrincipal;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.coretest.controller.template.CrudControllerTestTemplate;
import com.github.vincemann.springrapid.coretest.util.TransactionalRapidTestUtil;
import lombok.Getter;
import org.apache.catalina.users.AbstractUser;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
public class AclIntegrationCrudControllerTest
        <C extends GenericCrudController<?,?,S,?,?>, S extends CrudService<?,?>>
        extends AbstractAclIntegrationCrudControllerTest<C, CrudControllerTestTemplate>
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

//    @AfterEach
//    void cleanup(){
//        TransactionalRapidTestUtil.clear(service);
//    }
}
