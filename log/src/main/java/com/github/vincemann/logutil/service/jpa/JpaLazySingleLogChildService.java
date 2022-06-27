package com.github.vincemann.logutil.service.jpa;

import com.github.vincemann.logutil.model.EagerSingleLogChild;
import com.github.vincemann.logutil.model.LazySingleLogChild;
import com.github.vincemann.logutil.repo.EagerSingleLogChildRepository;
import com.github.vincemann.logutil.repo.LazySingleLogChildRepository;
import com.github.vincemann.logutil.service.EagerSingleLogChildService;
import com.github.vincemann.logutil.service.LazySingleLogChildService;
import com.github.vincemann.springrapid.core.service.JPACrudService;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import org.springframework.stereotype.Service;


@Service
@ServiceComponent
public class JpaLazySingleLogChildService extends JPACrudService<LazySingleLogChild,Long, LazySingleLogChildRepository> implements LazySingleLogChildService {

}