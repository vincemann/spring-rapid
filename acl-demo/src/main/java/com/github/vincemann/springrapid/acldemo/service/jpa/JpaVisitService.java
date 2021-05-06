package com.github.vincemann.springrapid.acldemo.service.jpa;

import com.github.vincemann.springrapid.acl.proxy.Acl;
import com.github.vincemann.springrapid.acl.proxy.Secured;
import com.github.vincemann.springrapid.core.proxy.annotation.CreateProxy;
import com.github.vincemann.springrapid.core.proxy.annotation.DefineProxy;
import com.github.vincemann.springrapid.core.service.JPACrudService;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.acldemo.model.Visit;
import com.github.vincemann.springrapid.acldemo.repositories.VisitRepository;
import com.github.vincemann.springrapid.acldemo.service.VisitService;
import org.springframework.aop.TargetClassAware;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;




@DefineProxy(name = "acl", extensions = {
        "authenticatedHasFullPermissionAboutSavedAclExtension",
        "vetsHaveReadPermission"
})
@DefineProxy(name = "secured", extensions = {
        "onlyVetCanCreateSecurityExtension"
})
@CreateProxy(qualifiers = Acl.class,proxies = "acl")
@CreateProxy(qualifiers = Secured.class,proxies = {"acl","secured"})
@Primary
@Service
@ServiceComponent
public class JpaVisitService extends JPACrudService<Visit,Long, VisitRepository> implements VisitService, TargetClassAware {
    @Override
    public Class<?> getTargetClass() {
        return JpaVisitService.class;
    }
}
