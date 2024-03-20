package com.github.vincemann.springrapid.acldemo.service.jpa;

import com.github.vincemann.springrapid.autobidir.EnableAutoBiDir;
import com.github.vincemann.springrapid.core.service.AbstractCrudService;
import com.github.vincemann.springrapid.acldemo.model.Specialty;
import com.github.vincemann.springrapid.acldemo.repo.SpecialtyRepository;
import com.github.vincemann.springrapid.acldemo.service.SpecialtyService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Primary
@Service
@EnableAutoBiDir
public class JpaSpecialtyService
        extends AbstractCrudService<Specialty,Long, SpecialtyRepository>
            implements SpecialtyService {

    @Override
    public Optional<Specialty> findByDescription(String description) {
        return getRepository().findByDescription(description);
    }
}
