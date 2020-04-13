package io.github.vincemann.springrapid.coretest.service.result;

/**
 * Equivalent of {@link org.springframework.test.web.servlet.ResultHandler} of {@link org.springframework.test.web.servlet.MockMvc}, but for
 * {@link io.github.vincemann.springrapid.coretest.service.ServiceTestTemplate}.
 */
public interface ServiceResultHandler {
    ServiceResultActions handle(ServiceTestContext context);
}
