package com.github.vincemann.logutil.service.jpa;

import com.github.vincemann.logutil.model.LogChild3;
import com.github.vincemann.logutil.repo.LogChild3Repository;
import com.github.vincemann.logutil.service.LogChild3Service;
import com.github.vincemann.springrapid.core.service.JPACrudService;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import org.springframework.stereotype.Service;

@Service
@ServiceComponent
public class JpaLogChild3Service extends JPACrudService<LogChild3,Long, LogChild3Repository>implements LogChild3Service {


}
