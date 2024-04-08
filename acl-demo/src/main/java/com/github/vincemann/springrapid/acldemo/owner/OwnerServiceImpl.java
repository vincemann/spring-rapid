package com.github.vincemann.springrapid.acldemo.owner;

import com.github.vincemann.springrapid.acldemo.owner.dto.UpdateOwnerDto;
import com.github.vincemann.springrapid.acldemo.pet.Pet;
import com.github.vincemann.springrapid.auth.service.AbstractUserService;
import com.github.vincemann.springrapid.auth.Root;
import com.github.vincemann.springrapid.auth.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.UpdateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.Validator;
import java.util.Optional;

import static com.github.vincemann.springrapid.core.util.RepositoryUtil.findPresentById;

@Root
@Service
public class OwnerServiceImpl
        extends AbstractUserService<Owner,Long, OwnerRepository>
                implements OwnerService
{

    private Validator validator;

    @Transactional(readOnly = true)
    @Override
    public Optional<Owner> findByLastName(String lastName) {
        return getRepository().findByLastName(lastName);
    }

    @Transactional
    @Override
    public Owner update(UpdateOwnerDto dto) throws EntityNotFoundException {
        Long id = dto.getId();
        Owner owner = findPresentById(getRepository(), id);
        new UpdateHelper(validator).copyProperties(dto,owner);
        return owner;
    }

    @Transactional
    @Override
    public void addPetSpectator(long permittedOwnerId, long targetOwnerId) throws EntityNotFoundException {
        Owner permittedOwner = findPresentById(getRepository(),permittedOwnerId);
        Owner targetOwner = findPresentById(getRepository(),targetOwnerId);

        for (Pet pet : targetOwner.getPets()) {
            getAclService().grantUserPermissionForEntity(permittedOwner.getContactInformation(), pet, BasePermission.READ);
        }
    }

    @Autowired
    public void setValidator(Validator validator) {
        this.validator = validator;
    }
}
