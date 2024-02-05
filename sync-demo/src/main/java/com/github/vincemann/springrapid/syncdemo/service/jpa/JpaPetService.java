package com.github.vincemann.springrapid.syncdemo.service.jpa;

import com.github.vincemann.springrapid.autobidir.EnableAutoBiDir;
import com.github.vincemann.springrapid.core.service.JpaCrudService;
import com.github.vincemann.springrapid.syncdemo.model.Pet;
import com.github.vincemann.springrapid.syncdemo.repo.PetRepository;
import com.github.vincemann.springrapid.syncdemo.service.PetService;
import com.github.vincemann.springrapid.syncdemo.service.Root;
import org.springframework.stereotype.Service;

@Root
@Service
@EnableAutoBiDir
public class JpaPetService
        extends JpaCrudService<Pet, Long, PetRepository>
                implements PetService {

    @Override
    public Class<?> getTargetClass() {
        return JpaPetService.class;
    }

}
