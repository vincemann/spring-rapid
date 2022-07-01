package com.github.vincemann.logutil.service.jpa;

import com.github.vincemann.logutil.model.LogChild2;
import com.github.vincemann.logutil.repo.LogChild2Repository;
import com.github.vincemann.logutil.service.LogChild2Service;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.JPACrudService;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import org.springframework.stereotype.Service;

@Service
@ServiceComponent
public class JpaLogChild2Service extends JPACrudService<LogChild2,Long, LogChild2Repository> implements LogChild2Service {
}
