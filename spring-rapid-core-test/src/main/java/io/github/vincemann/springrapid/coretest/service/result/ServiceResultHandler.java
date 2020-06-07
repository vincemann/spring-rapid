package io.github.vincemann.springrapid.coretest.service.result;

import io.github.vincemann.springrapid.coretest.service.ServiceTestTemplate;

/**
 * Equivalent of {@link org.springframework.test.web.servlet.ResultHandler} of {@link org.springframework.test.web.servlet.MockMvc}, but for
 * {@link io.github.vincemann.springrapid.coretest.service.ServiceTestTemplate}.
 */
public interface ServiceResultHandler {
    /**
     * Get {@link ServiceTestContext} via {@link ServiceTestTemplate#getTestContext()}
     */
    void handle();
}
