package com.github.vincemann.springrapid.acldemo.service.jpa;

import com.github.vincemann.springrapid.autobidir.EnableAutoBiDir;
import com.github.vincemann.springrapid.core.service.JpaCrudService;
import com.github.vincemann.springrapid.acldemo.model.PetType;
import com.github.vincemann.springrapid.acldemo.repo.PetTypeRepository;
import com.github.vincemann.springrapid.acldemo.service.PetTypeService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Primary
@Service
public class JpaPetTypeService
        extends JpaCrudService<PetType,Long,PetType,PetTypeRepository>
            implements PetTypeService {

    @Transactional
    @Override
    public PetType create(PetType petType) throws EntityNotFoundException, BadEntityException {
        return getRepository().save(petType);
    }
}
