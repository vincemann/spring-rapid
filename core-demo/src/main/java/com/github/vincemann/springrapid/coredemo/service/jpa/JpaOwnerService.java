package com.github.vincemann.springrapid.coredemo.service.jpa;

import com.github.vincemann.springrapid.autobidir.EnableAutoBiDir;
import com.github.vincemann.springrapid.core.service.AbstractCrudService;
import com.github.vincemann.springrapid.coredemo.model.Owner;
import com.github.vincemann.springrapid.coredemo.repo.OwnerRepository;
import com.github.vincemann.springrapid.coredemo.service.OwnerService;
import com.github.vincemann.springrapid.coredemo.service.Root;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Root
@Service
@EnableAutoBiDir
public class JpaOwnerService
        extends AbstractCrudService<Owner,Long, OwnerRepository>
                implements OwnerService
{

    public static final String OWNER_OF_THE_YEARS_NAME = "Chad";


    @Transactional
    @Override
    public Optional<Owner> findByLastName(String lastName) {
        return getRepository().findByLastName(lastName);
    }

    /**
     * Owner named "chad" is owner of the year
     * @return
     */
    @Transactional
    @Override
    public Optional<Owner> findOwnerOfTheYear() {
        return getRepository().findAll().stream()
                .filter(owner -> owner.getFirstName().equals(OWNER_OF_THE_YEARS_NAME))
                .findFirst();
    }

}
