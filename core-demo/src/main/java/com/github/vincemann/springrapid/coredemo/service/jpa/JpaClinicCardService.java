package com.github.vincemann.springrapid.coredemo.service.jpa;

import com.github.vincemann.springrapid.core.service.JPACrudService;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.coredemo.model.ClinicCard;
import com.github.vincemann.springrapid.coredemo.repo.ClinicCardRepository;
import com.github.vincemann.springrapid.coredemo.service.ClinicCardService;
import org.springframework.stereotype.Service;

@ServiceComponent
public class JpaClinicCardService extends JPACrudService<ClinicCard,Long, ClinicCardRepository> implements ClinicCardService {

    @Override
    public Class<?> getTargetClass() {
        return JpaClinicCardService.class;
    }
}
