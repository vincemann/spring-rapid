package com.github.vincemann.springlemon.auth.service;

import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.domain.AbstractUserRepository;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;

@Validated
@ServiceComponent
public interface LemonService<U extends AbstractUser<ID>, ID extends Serializable, R extends AbstractUserRepository<U,ID>>
        extends CrudService<U,ID, R>, AopLoggable, SimpleLemonService<U,ID> {
}
