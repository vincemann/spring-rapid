package com.github.vincemann.springrapid.coretest.service.result;

import com.github.vincemann.springrapid.coretest.service.ServiceTestTemplate;

/**
 * Equivalent of {@link org.springframework.test.web.servlet.ResultHandler} of {@link org.springframework.test.web.servlet.MockMvc}, but for
 * {@link ServiceTestTemplate}.
 */
public interface ServiceResultHandler {
    void handle();
}
