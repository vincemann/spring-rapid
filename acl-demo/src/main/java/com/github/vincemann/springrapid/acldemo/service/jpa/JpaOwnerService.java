package com.github.vincemann.springrapid.acldemo.service.jpa;

import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import com.github.vincemann.springrapid.acldemo.MyRoles;
import com.github.vincemann.springrapid.acldemo.model.User;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.core.service.JPACrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.stereotype.Component;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.repo.OwnerRepository;
import com.github.vincemann.springrapid.acldemo.service.OwnerService;
import com.github.vincemann.springrapid.acldemo.service.Root;
import org.springframework.aop.TargetClassAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Root
@Primary
@Component
public class JpaOwnerService
        extends JPACrudService<Owner,Long, OwnerRepository>
                implements OwnerService, AopLoggable, TargetClassAware {

    public static final String OWNER_OF_THE_YEARS_NAME = "Chad";

    private UserService<User,?> userService;

    @LogInteraction
    @Transactional
    @Override
    public Optional<Owner> findByLastName(String lastName) {
        return getRepository().findByLastName(lastName);
    }


    public Class<?> getTargetClass(){
        return JpaOwnerService.class;
    }


    @Transactional
    @Override
    public Owner save(Owner entity) throws BadEntityException {
        User user = entity.getUser();
        if (user == null){
            throw new BadEntityException("Cant save owner without mapped user");
        }
        user.getRoles().add(MyRoles.OWNER);
        try {
            userService.fullUpdate(user);
        } catch (EntityNotFoundException e) {
            throw new RuntimeException(e);
        }
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

    @Autowired
    public void injectUserService(UserService<User,?> userService) {
        this.userService = userService;
    }
}
