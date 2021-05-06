package com.github.vincemann.springrapid.acldemo.service.jpa;

import com.github.vincemann.aoplog.api.LogInteraction;
import com.github.vincemann.springrapid.acl.proxy.Acl;
import com.github.vincemann.springrapid.acl.proxy.Secured;

import com.github.vincemann.springrapid.acldemo.auth.MyRoles;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.model.User;
import com.github.vincemann.springrapid.acldemo.model.Visit;
import com.github.vincemann.springrapid.core.proxy.annotation.CreateProxy;
import com.github.vincemann.springrapid.core.proxy.annotation.DefineProxy;
import com.github.vincemann.springrapid.core.service.JPACrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.acldemo.model.Vet;
import com.github.vincemann.springrapid.acldemo.repositories.VetRepository;
import com.github.vincemann.springrapid.acldemo.service.VetService;
import org.springframework.aop.TargetClassAware;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;





@DefineProxy(name = "acl", extensions = {
        "authenticatedHasFullPermissionAboutSavedAclExtension",
        "authenticatedHasFullPermissionAboutSavedContainedUserAclExtension"
})
@DefineProxy(name = "secured")
@CreateProxy(qualifiers = Acl.class,proxies = "acl")
@CreateProxy(qualifiers = Secured.class,proxies = {"acl","secured"})
@Primary
@Service
@ServiceComponent
public class JpaVetService
        extends JPACrudService<Vet,Long, VetRepository>
        implements VetService, TargetClassAware {



    @Override
    public Class<?> getTargetClass() {
        return JpaVetService.class;
    }

    @LogInteraction
    @Transactional
    @Override
    public Optional<Vet> findByLastName(String lastName) {
        return getRepository().findByLastName(lastName);
    }

    @Transactional
    @Override
    public Vet save(Vet entity) throws BadEntityException {
        User user = entity.getUser();
        if (user == null){
            throw new BadEntityException("Cant save vet without mapped user");
        }
        user.getRoles().add(MyRoles.NEW_VET);
        return super.save(entity);
    }

    @Override
    public void giveOwnerReadPermissionForVisit(Owner owner, Visit visit) {

    }
}
