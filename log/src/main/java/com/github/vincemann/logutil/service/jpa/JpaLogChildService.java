package com.github.vincemann.logutil.service.jpa;

import com.github.vincemann.logutil.model.EagerSingleLogChild;
import com.github.vincemann.logutil.model.LogChild;
import com.github.vincemann.logutil.repo.EagerSingleLogChildRepository;
import com.github.vincemann.logutil.repo.LogChildRepository;
import com.github.vincemann.logutil.service.EagerSingleLogChildService;
import com.github.vincemann.logutil.service.LogChildService;
import com.github.vincemann.springrapid.core.service.JPACrudService;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import org.springframework.stereotype.Service;


@Service
@ServiceComponent
//@DisableAutoBiDir
public class JpaLogChildService extends JPACrudService<LogChild,Long, LogChildRepository> implements LogChildService {

}