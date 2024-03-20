package com.github.vincemann.springrapid.syncdemo.service.jpa;

import com.github.vincemann.springrapid.autobidir.EnableAutoBiDir;
import com.github.vincemann.springrapid.core.service.JpaCrudService;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.sync.EnableAuditCollection;
import com.github.vincemann.springrapid.syncdemo.model.Pet;
import com.github.vincemann.springrapid.syncdemo.repo.PetRepository;
import com.github.vincemann.springrapid.syncdemo.service.PetService;
import com.github.vincemann.springrapid.syncdemo.service.Root;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Root
@Service
@EnableAutoBiDir
public class JpaPetService
        extends JpaCrudService<Pet, Long, PetRepository>
                implements PetService {

    @Transactional
    @EnableAuditCollection
    @Override
    public Pet partialUpdate(Pet update, String... fieldsToUpdate) throws EntityNotFoundException {
        return super.partialUpdate(update, fieldsToUpdate);
    }

}
