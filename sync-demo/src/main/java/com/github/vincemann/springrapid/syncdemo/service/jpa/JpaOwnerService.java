package com.github.vincemann.springrapid.syncdemo.service.jpa;

import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import com.github.vincemann.springrapid.autobidir.EnableAutoBiDir;
import com.github.vincemann.springrapid.core.service.JpaCrudService;
import com.github.vincemann.springrapid.sync.EnableAuditCollection;
import com.github.vincemann.springrapid.syncdemo.model.Owner;
import com.github.vincemann.springrapid.syncdemo.repo.OwnerRepository;
import com.github.vincemann.springrapid.syncdemo.service.OwnerService;
import com.github.vincemann.springrapid.syncdemo.service.Root;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Root
@Service
@EnableAutoBiDir
@EnableAuditCollection
@LogInteraction
public class JpaOwnerService
        extends JpaCrudService<Owner,Long, OwnerRepository>
                implements OwnerService  {

    public static final String OWNER_OF_THE_YEAR_NAME = "Chad";

    @Transactional(readOnly = true)
    @Override
    public Optional<Owner> findByLastName(String lastName) {
        return getRepository().findByLastName(lastName);
    }


    @Transactional(readOnly = true)
    @Override
    public Optional<Owner> findOwnerOfTheYear() {
        return getRepository().findAll().stream().filter(owner -> {
            return owner.getFirstName().equals(OWNER_OF_THE_YEAR_NAME);
        }).findFirst();
    }
}
