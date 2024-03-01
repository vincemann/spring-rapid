package com.github.vincemann.springrapid.acldemo.service.jpa;

import com.github.vincemann.springrapid.acl.service.RapidAclService;
import com.github.vincemann.springrapid.acldemo.MyRoles;
import com.github.vincemann.springrapid.acldemo.model.Pet;
import com.github.vincemann.springrapid.acldemo.repo.PetRepository;
import com.github.vincemann.springrapid.acldemo.service.PetService;
import com.github.vincemann.springrapid.core.service.JpaCrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Primary
@Service
public class JpaPetService
        extends JpaCrudService<Pet, Long, PetRepository>
                implements PetService {

    private RapidAclService aclService;

    @Transactional
    @Override
    public Pet create(Pet entity) throws BadEntityException {
        Pet pet = super.create(entity);
        saveAclInfo(pet);
        return pet;
    }

    @Transactional
    @Override
    public Optional<Pet> findNyName(String name) {
        return getRepository().findByName(name);
    }

    private void saveAclInfo(Pet pet){
        // authenticated gains admin permission
        aclService.grantAuthenticatedPermissionForEntity(pet, BasePermission.ADMINISTRATION);
        // owner of pet gains admin permission
        aclService.grantUserPermissionForEntity(pet.getOwner().getContactInformation(), pet, BasePermission.ADMINISTRATION);
        // vets can read & write
        aclService.grantRolePermissionForEntity(MyRoles.VET, pet, BasePermission.READ, BasePermission.WRITE);
    }

    @Override
    public Class<?> getTargetClass() {
        return JpaPetService.class;
    }

    @Autowired
    public void setAclService(RapidAclService aclService) {
        this.aclService = aclService;
    }
}
