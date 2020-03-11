package io.github.vincemann.generic.crud.lib.test.service.result.matcher;

import io.github.vincemann.generic.crud.lib.test.service.result.ServiceResult;
import io.github.vincemann.generic.crud.lib.test.service.result.ServiceTestContext;
import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.CrudRepository;

public interface ServiceResultMatcher {
    public void match(ServiceTestContext context);
}
