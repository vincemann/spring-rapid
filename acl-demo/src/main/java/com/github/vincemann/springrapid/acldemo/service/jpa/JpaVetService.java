package com.github.vincemann.springrapid.acldemo.service.jpa;

import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import com.github.vincemann.springrapid.acl.proxy.Acl;
import com.github.vincemann.springrapid.acl.proxy.Secured;

import com.github.vincemann.springrapid.acldemo.MyRoles;
import com.github.vincemann.springrapid.acldemo.model.User;
import com.github.vincemann.springrapid.acldemo.service.ext.acl.UserGainsAdminPermissionOnContainedUserOnCreate;
import com.github.vincemann.springrapid.auth.service.extension.UserGainsAdminPermissionOnCreated;
import com.github.vincemann.springrapid.core.proxy.annotation.CreateProxy;
import com.github.vincemann.springrapid.core.proxy.annotation.DefineProxy;
import com.github.vincemann.springrapid.core.service.JpaCrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.acldemo.model.Vet;
import com.github.vincemann.springrapid.acldemo.repo.VetRepository;
import com.github.vincemann.springrapid.acldemo.service.VetService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;





@DefineProxy(name = "acl", extensionClasses = {
        UserGainsAdminPermissionOnCreated.class,
        UserGainsAdminPermissionOnContainedUserOnCreate.class
})
@DefineProxy(name = "secured")
@CreateProxy(qualifiers = Acl.class,proxies = "acl")
@CreateProxy(qualifiers = Secured.class,proxies = {"acl","secured"})
@Primary
@Service

public class JpaVetService
        extends JpaCrudService<Vet,Long, VetRepository>
                implements VetService {



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
    public Vet create(Vet entity) throws BadEntityException {
        User user = entity.getUser();
        if (user == null){
            throw new BadEntityException("Cant save vet without mapped user");
        }
        user.getRoles().add(MyRoles.NEW_VET);
        return super.create(entity);
    }

}
