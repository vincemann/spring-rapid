package com.github.vincemann.springrapid.acldemo.service.jpa;

import com.github.vincemann.springrapid.acl.proxy.Acl;
import com.github.vincemann.springrapid.acl.proxy.Secured;
import com.github.vincemann.springrapid.acldemo.model.Pet;
import com.github.vincemann.springrapid.acldemo.repo.PetRepository;
import com.github.vincemann.springrapid.acldemo.service.PetService;
import com.github.vincemann.springrapid.acldemo.service.ext.sec.OwnerCanOnlyCreateOwnPets;
import com.github.vincemann.springrapid.core.proxy.annotation.CreateProxy;
import com.github.vincemann.springrapid.core.proxy.annotation.DefineProxy;
import com.github.vincemann.springrapid.core.service.JpaCrudService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;


@DefineProxy(name = "acl", extensions = {
        "authenticatedGainsAdminPermissionOnSave",
        "ownerGainsAdminPermissionOnSave",
        "vetGainsAdminPermissionOnSave"
})
@DefineProxy(name = "secured", extensionClasses = {
        OwnerCanOnlyCreateOwnPets.class
})
@CreateProxy(qualifiers = Acl.class,proxies = "acl")
@CreateProxy(qualifiers = Secured.class,proxies = {"acl","secured"})
@Primary
@Service
public class JpaPetService
        extends JpaCrudService<Pet, Long, PetRepository>
                implements PetService {
    @Override
    public Class<?> getTargetClass() {
        return JpaPetService.class;
    }
}
