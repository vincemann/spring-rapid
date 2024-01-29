package com.github.vincemann.springrapid.syncdemo.service.jpa;

import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import com.github.vincemann.springrapid.core.service.JPACrudService;
import org.springframework.stereotype.Component;
import com.github.vincemann.springrapid.syncdemo.model.Owner;
import com.github.vincemann.springrapid.syncdemo.repo.OwnerRepository;
import com.github.vincemann.springrapid.syncdemo.service.OwnerService;
import com.github.vincemann.springrapid.syncdemo.service.Root;
import org.springframework.aop.TargetClassAware;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Root
@Component
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




    /**
     * Owner named "chad" is owner of the year
     * @return
     */
    @Transactional
    @Override
    public Optional<Owner> findOwnerOfTheYear() {
        return getRepository().findAll().stream().filter(owner -> {
            return owner.getFirstName().equals(OWNER_OF_THE_YEARS_NAME);
        }).findFirst();
    }

//    @Override
//    @Transactional
//    public Owner lazyLoadFindById(Long id) {
//        Owner owner = getRepository().findById(id).get();
//        owner.getLazyLoadedItems().size();
//        return owner;
//    }

}
