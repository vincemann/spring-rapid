package com.github.vincemann.springrapid.acldemo.service.jpa;

import com.github.vincemann.springrapid.acldemo.model.Illness;
import com.github.vincemann.springrapid.core.service.JpaCrudService;
import com.github.vincemann.springrapid.acldemo.repo.IllnessRepository;
import com.github.vincemann.springrapid.acldemo.service.IllnessService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Primary
@Service
public class JpaIllnessService
        extends JpaCrudService<Illness,Long, IllnessRepository>
            implements IllnessService {

    @Override
    public Optional<Illness> findByName(String name) {
        return getRepository().findByName(name);
    }

}
