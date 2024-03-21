package com.github.vincemann.springrapid.acldemo.service.jpa.sec;

import com.github.vincemann.springrapid.acl.Secured;
import com.github.vincemann.springrapid.acl.service.SecuredServiceDecorator;
import com.github.vincemann.springrapid.acldemo.Roles;
import com.github.vincemann.springrapid.acldemo.dto.pet.CreatePetDto;
import com.github.vincemann.springrapid.acldemo.dto.pet.OwnerUpdatesPetDto;
import com.github.vincemann.springrapid.acldemo.dto.pet.UpdateIllnessDto;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.model.Pet;
import com.github.vincemann.springrapid.acldemo.repo.OwnerRepository;
import com.github.vincemann.springrapid.acldemo.service.PetService;
import com.github.vincemann.springrapid.core.sec.AuthorizationUtils;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.RepositoryUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.github.vincemann.springrapid.core.util.RepositoryUtil.findPresentById;

@Secured
@Service
public class SecuredPetService
        extends SecuredServiceDecorator<PetService>
        implements PetService
{

    @Autowired
    public SecuredPetService(PetService decorated) {
        super(decorated);
    }

    @Transactional
    @Override
    public Pet create(CreatePetDto pet) throws BadEntityException, EntityNotFoundException {
        // need create permission on owner in order to create pet for him
        getAclTemplate().checkPermission(pet.getOwnerId(),Owner.class, BasePermission.CREATE);
        return getDecorated().create(pet);
    }

    @Transactional
    @Override
    public Pet removeIllness(UpdateIllnessDto dto) throws EntityNotFoundException, BadEntityException {
        AuthorizationUtils.assertHasRoles(Roles.VET);
        getAclTemplate().checkPermission(dto.getId(),Pet.class, BasePermission.WRITE);
        return getDecorated().removeIllness(dto);
    }

    @Transactional
    @Override
    public Pet addIllnesses(UpdateIllnessDto dto) throws EntityNotFoundException, BadEntityException {
        AuthorizationUtils.assertHasRoles(Roles.VET);
        getAclTemplate().checkPermission(dto.getId(),Pet.class, BasePermission.WRITE);
        return getDecorated().addIllnesses(dto);
    }

    @Transactional
    @Override
    public Pet ownerUpdatesPet(OwnerUpdatesPetDto dto) throws EntityNotFoundException, BadEntityException {
        AuthorizationUtils.assertHasRoles(Roles.OWNER);
        getAclTemplate().checkPermission(dto.getId(),Pet.class, BasePermission.WRITE);
        return getDecorated().ownerUpdatesPet(dto);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Pet> findByName(String name) {
        Optional<Pet> pet = getDecorated().findByName(name);
        pet.ifPresent(p -> getAclTemplate().checkPermission(p,BasePermission.READ));
        return pet;
    }
}
