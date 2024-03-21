package com.github.vincemann.springrapid.acldemo.service.jpa.sec;

import com.github.vincemann.springrapid.acl.Secured;
import com.github.vincemann.springrapid.acl.service.SecuredServiceDecorator;
import com.github.vincemann.springrapid.acldemo.dto.visit.CreateVisitDto;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.model.Pet;
import com.github.vincemann.springrapid.acldemo.model.Vet;
import com.github.vincemann.springrapid.acldemo.model.Visit;
import com.github.vincemann.springrapid.acldemo.repo.OwnerRepository;
import com.github.vincemann.springrapid.acldemo.repo.PetRepository;
import com.github.vincemann.springrapid.acldemo.repo.VetRepository;
import com.github.vincemann.springrapid.acldemo.service.VisitService;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.core.sec.AuthorizationUtils;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.VerifyAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.github.vincemann.springrapid.core.util.RepositoryUtil.findPresentById;

@Secured
@Service
public class SecuredVisitService
        extends SecuredServiceDecorator<VisitService>
        implements VisitService
{
    private VetRepository vetRepository;
    private OwnerRepository ownerRepository;
    private PetRepository petRepository;

    @Autowired
    public SecuredVisitService(VisitService decorated) {
        super(decorated);
    }


    @Transactional
    @PreAuthorize("hasPermission(#visitId, 'com.github.vincemann.springrapid.acldemo.model.Visit', 'administration')")
    @Override
    public void addSpectator(Long spectatorId, Long visitId) throws EntityNotFoundException {
        getDecorated().addSpectator(spectatorId,visitId);
    }

    @Transactional
    @PreAuthorize("hasPermission(#visitId, 'com.github.vincemann.springrapid.acldemo.model.Visit', 'administration')")
    @Override
    public void removeSpectator(Long spectatorId, Long visitId) throws EntityNotFoundException {
        getDecorated().removeSpectator(spectatorId,visitId);
    }


    @Transactional
    @Override
    public Visit create(CreateVisitDto dto) throws BadEntityException, EntityNotFoundException {
        // must not be unverified
        AuthorizationUtils.assertNotHasRoles(AuthRoles.UNVERIFIED);

        // need to have create permission on vet
        Vet vet = findPresentById(vetRepository, dto.getVetId());
        getAclTemplate().checkPermission(vet, BasePermission.CREATE);

        // cant create visit for owner with pets not owned by owner
        Owner owner = findPresentById(ownerRepository, dto.getOwnerId());
        for (Long petId : dto.getPetIds()) {
            Pet pet = findPresentById(petRepository, petId);
            VerifyAccess.condition(pet.getOwner().equals(owner),
                    "cant create visit for owner with pets not owned by owner");
        }
        return getDecorated().create(dto);
    }

    @Autowired
    public void setOwnerRepository(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    @Autowired
    public void setPetRepository(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    @Autowired
    public void setVetRepository(VetRepository vetRepository) {
        this.vetRepository = vetRepository;
    }
}
