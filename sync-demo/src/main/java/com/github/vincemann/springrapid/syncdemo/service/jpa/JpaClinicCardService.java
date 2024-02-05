package com.github.vincemann.springrapid.syncdemo.service.jpa;

import com.github.vincemann.springrapid.core.service.JPACrudService;
import org.springframework.stereotype.Component;
import com.github.vincemann.springrapid.syncdemo.model.ClinicCard;
import com.github.vincemann.springrapid.syncdemo.repo.ClinicCardRepository;
import com.github.vincemann.springrapid.syncdemo.service.ClinicCardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class JpaClinicCardService
        extends JPACrudService<ClinicCard,Long, ClinicCardRepository>
            implements ClinicCardService
{

    @Override
    public Class<?> getTargetClass() {
        return JpaClinicCardService.class;
    }
}

