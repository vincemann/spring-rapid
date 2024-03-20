package com.github.vincemann.springrapid.acldemo.service.jpa;

import com.github.vincemann.springrapid.acldemo.dto.CreateSpecialtyDto;
import com.github.vincemann.springrapid.acldemo.model.Specialty;
import com.github.vincemann.springrapid.acldemo.repo.SpecialtyRepository;
import com.github.vincemann.springrapid.acldemo.service.SpecialtyService;
import com.github.vincemann.springrapid.core.service.JpaCrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Primary
@Service
public class JpaSpecialtyService
        extends JpaCrudService<Specialty,Long, CreateSpecialtyDto, SpecialtyRepository>
            implements SpecialtyService {

    @Transactional
    @Override
    public Specialty create(CreateSpecialtyDto dto) throws EntityNotFoundException, BadEntityException {
        return getRepository().save(map(dto));
    }

    Specialty map(CreateSpecialtyDto dto){
        return Specialty.builder().description(dto.getDescription()).build();
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Specialty> findByDescription(String description) {
        return getRepository().findByDescription(description);
    }
}
