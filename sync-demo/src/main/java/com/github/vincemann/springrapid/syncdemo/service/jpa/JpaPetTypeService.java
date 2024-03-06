package com.github.vincemann.springrapid.syncdemo.service.jpa;

import com.github.vincemann.springrapid.autobidir.EnableAutoBiDir;
import com.github.vincemann.springrapid.core.service.JpaCrudService;
import com.github.vincemann.springrapid.syncdemo.model.PetType;
import com.github.vincemann.springrapid.syncdemo.repo.PetTypeRepository;
import com.github.vincemann.springrapid.syncdemo.service.PetTypeService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service
@EnableAutoBiDir
public class JpaPetTypeService
        extends JpaCrudService<PetType,Long, PetTypeRepository>
                implements PetTypeService {
}
