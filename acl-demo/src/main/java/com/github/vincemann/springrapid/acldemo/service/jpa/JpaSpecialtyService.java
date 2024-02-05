package com.github.vincemann.springrapid.acldemo.service.jpa;

import com.github.vincemann.springrapid.core.service.JpaCrudService;
import com.github.vincemann.springrapid.acldemo.model.Specialty;
import com.github.vincemann.springrapid.acldemo.repo.SpecialtyRepository;
import com.github.vincemann.springrapid.acldemo.service.SpecialtyService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Primary
@Service
public class JpaSpecialtyService
        extends JpaCrudService<Specialty,Long, SpecialtyRepository>
            implements SpecialtyService {

    @Override
    public Optional<Specialty> findByDescription(String description) {
        return getRepository().findByDescription(description);
    }

    @Override
    public Class<?> getTargetClass() {
        return JpaSpecialtyService.class;
    }
}
