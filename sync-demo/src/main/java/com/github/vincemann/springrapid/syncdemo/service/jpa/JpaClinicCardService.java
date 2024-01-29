package com.github.vincemann.springrapid.syncdemo.service.jpa;

import com.github.vincemann.springrapid.core.service.JPACrudService;
import org.springframework.stereotype.Component;
import com.github.vincemann.springrapid.syncdemo.model.ClinicCard;
import com.github.vincemann.springrapid.syncdemo.repo.ClinicCardRepository;
import com.github.vincemann.springrapid.syncdemo.service.ClinicCardService;

@Component
//@DisableAutoBiDir
public class JpaClinicCardService
        extends JPACrudService<ClinicCard,Long, ClinicCardRepository>
            implements ClinicCardService
{

    @Override
    public Class<?> getTargetClass() {
        return JpaClinicCardService.class;
    }
}

