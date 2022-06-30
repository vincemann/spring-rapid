package com.github.vincemann.logutil.service.jpa;

import com.github.vincemann.logutil.model.EagerSingleLogChild;
import com.github.vincemann.logutil.model.LogParent;
import com.github.vincemann.logutil.repo.EagerSingleLogChildRepository;
import com.github.vincemann.logutil.repo.LogParentRepository;
import com.github.vincemann.logutil.service.EagerSingleLogChildService;
import com.github.vincemann.logutil.service.LogParentService;
import com.github.vincemann.springrapid.core.service.JPACrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@ServiceComponent
public class JpaLogParentService extends JPACrudService<LogParent, Long, LogParentRepository> implements LogParentService {

}
