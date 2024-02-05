package com.github.vincemann.springrapid.syncdemo.service.jpa;

import com.github.vincemann.springrapid.autobidir.EnableAutoBiDir;
import com.github.vincemann.springrapid.core.service.JpaCrudService;
import com.github.vincemann.springrapid.syncdemo.model.ClinicCard;
import com.github.vincemann.springrapid.syncdemo.repo.ClinicCardRepository;
import com.github.vincemann.springrapid.syncdemo.service.ClinicCardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@EnableAutoBiDir
public class JpaClinicCardService
        extends JpaCrudService<ClinicCard,Long, ClinicCardRepository>
            implements ClinicCardService
{

    @Override
    public Class<?> getTargetClass() {
        return JpaClinicCardService.class;
    }
}

