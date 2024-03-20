package com.github.vincemann.springrapid.coredemo.service.jpa;

import com.github.vincemann.springrapid.autobidir.EnableAutoBiDir;
import com.github.vincemann.springrapid.core.service.AbstractCrudService;
import org.springframework.context.annotation.Primary;
import com.github.vincemann.springrapid.coredemo.model.ClinicCard;
import com.github.vincemann.springrapid.coredemo.repo.ClinicCardRepository;
import com.github.vincemann.springrapid.coredemo.service.ClinicCardService;
import org.springframework.stereotype.Service;

@Primary
@Service
@EnableAutoBiDir
public class JpaClinicCardService
        extends AbstractCrudService<ClinicCard,Long, ClinicCardRepository>
            implements ClinicCardService
{
}

