package com.github.vincemann.springrapid.acldemo.service.jpa;

import com.github.vincemann.springrapid.acldemo.model.Illness;
import com.github.vincemann.springrapid.core.service.JPACrudService;
import org.springframework.stereotype.Component;
import com.github.vincemann.springrapid.acldemo.repo.IllnessRepository;
import com.github.vincemann.springrapid.acldemo.service.IllnessService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Primary
@Service
midEgDuration()
public class JpaIllnessService extends JPACrudService<Illness,Long, IllnessRepository> implements IllnessService {

    @Override
    public Optional<Illness> findByName(String name) {
        return getRepository().findByName(name);
    }

    @Override
    public Class<?> getTargetClass() {
        return JpaIllnessService.class;
    }
}
