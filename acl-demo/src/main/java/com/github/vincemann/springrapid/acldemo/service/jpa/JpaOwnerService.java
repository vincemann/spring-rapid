package com.github.vincemann.springrapid.acldemo.service.jpa;

import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.LogInteraction;
import com.github.vincemann.springrapid.acldemo.auth.MyRoles;
import com.github.vincemann.springrapid.acldemo.model.User;
import com.github.vincemann.springrapid.core.service.JPACrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.repositories.OwnerRepository;
import com.github.vincemann.springrapid.acldemo.service.OwnerService;
import com.github.vincemann.springrapid.acldemo.service.Root;
import org.springframework.aop.TargetClassAware;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Root
@Primary
@ServiceComponent
public class JpaOwnerService
        extends JPACrudService<Owner,Long, OwnerRepository>
                implements OwnerService, AopLoggable, TargetClassAware {

    public static final String OWNER_OF_THE_YEARS_NAME = "Chad";

    @LogInteraction
    @Transactional
    @Override
    public Optional<Owner> findByLastName(String lastName) {
        return getRepository().findByLastName(lastName);
    }


    public Class<?> getTargetClass(){
        return JpaOwnerService.class;
    }


    @Override
    public Owner save(Owner entity) throws BadEntityException {
        User user = entity.getUser();
        if (user == null){
            throw new BadEntityException("Cant save owner without mapped user");
        }
        user.getRoles().add(MyRoles.OWNER);
        return super.save(entity);
    }

    /**
     * Owner named "42" is owner of the year
     * @return
     */
    @Transactional
    @Override
    public Optional<Owner> findOwnerOfTheYear() {
        return getRepository().findAll().stream().filter(owner -> {
            return owner.getFirstName().equals(OWNER_OF_THE_YEARS_NAME);
        }).findFirst();
    }
}
