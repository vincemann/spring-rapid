package com.github.vincemann.springrapid.acldemo.service.jpa;

import com.github.vincemann.springrapid.acl.proxy.Acl;
import com.github.vincemann.springrapid.acl.proxy.Secured;
import com.github.vincemann.springrapid.acl.service.extensions.AuthenticatedFullAccessAboutSavedAclExtension;
import com.github.vincemann.springrapid.acldemo.service.extensions.AuthenticatedFullAccessAboutSavedContainedUserAclExtension;
import com.github.vincemann.springrapid.acldemo.service.extensions.OwnerCanOnlySaveOwnPetsSecurityExtension;
import com.github.vincemann.springrapid.acldemo.service.extensions.OwnerFullAccessAboutSavedPetAclExtension;
import com.github.vincemann.springrapid.core.proxy.annotation.CreateProxy;
import com.github.vincemann.springrapid.core.proxy.annotation.DefineProxy;
import com.github.vincemann.springrapid.core.service.JPACrudService;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.acldemo.model.Pet;
import com.github.vincemann.springrapid.acldemo.repositories.PetRepository;
import com.github.vincemann.springrapid.acldemo.service.PetService;
import com.github.vincemann.springrapid.acldemo.service.Root;
import org.springframework.aop.TargetClassAware;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;


@DefineProxy(name = "acl", extensions = {
        AuthenticatedFullAccessAboutSavedAclExtension.class,
        OwnerFullAccessAboutSavedPetAclExtension.class
})
@DefineProxy(name = "secured", extensions = {
        OwnerCanOnlySaveOwnPetsSecurityExtension.class
})
@CreateProxy(qualifiers = Acl.class,proxies = "acl")
@CreateProxy(qualifiers = Secured.class,proxies = {"acl","secured"})
@Root
@Primary
@Service
@ServiceComponent
public class JpaPetService extends JPACrudService<Pet, Long, PetRepository> implements PetService, TargetClassAware {
    @Override
    public Class<?> getTargetClass() {
        return JpaPetService.class;
    }
}
