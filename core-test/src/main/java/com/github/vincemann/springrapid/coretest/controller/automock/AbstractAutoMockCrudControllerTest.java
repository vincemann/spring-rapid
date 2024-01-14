package com.github.vincemann.springrapid.coretest.controller.automock;

import com.github.vincemann.springrapid.core.controller.GenericCrudController;
import com.github.vincemann.springrapid.core.slicing.RapidProfiles;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.core.slicing.ServiceConfig;
import com.github.vincemann.springrapid.coretest.controller.AbstractMvcTest;
import com.github.vincemann.springrapid.coretest.slicing.RapidTestProfiles;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.web.servlet.MockMvc;

/**
 * BaseClass for Controller Integration tests performing basic {@link MockMvc} auto-config.
 * Does not load {@link ServiceComponent}s and {@link ServiceConfig}s.
 * All service beans are automatically mocked by {@link AutoMockServiceBeansGenericAnnotationWebConfigContextLoader}.
 * You can get the mocks by using @{@link org.springframework.beans.factory.annotation.Autowired}.
 * Mocks are reset after each test-method and shared across test-context.
 * This means that mocks are also shared between test classes if the test-context is cached (which is intended).
 *
 * Automocking of beans with @{@link org.springframework.beans.factory.annotation.Qualifier}s is also supported
 * -> diff mocks for diff qualifiers are created.
 * But method/constructor injection is required!
 *
 * Spring Context is configured for a minimal AutoConfigSetup for webTests.
 *
 * NOTE:
 * Dont specify profiles in property files, otherwise they will be activated by this test.
 * Instead always activate profiles via {@link ActiveProfiles}, cli-params or your IDE run config.
 */
@ActiveProfiles(value = {RapidTestProfiles.TEST, RapidProfiles.WEB, RapidTestProfiles.WEB_TEST})
@TestExecutionListeners(value = {
        AutoMockBeansTestExecutionListener.class},
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
//complete service interaction is mocked -> no interaction with database -> no datasource config needed
@ImportAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
@ContextConfiguration(
        loader = AutoMockServiceBeansGenericAnnotationWebConfigContextLoader.class/*,
        classes = PropertyPlaceholderAutoConfiguration.class*/,
        initializers = ConfigFileApplicationContextInitializer.class
)
public abstract class AbstractAutoMockCrudControllerTest
        <C extends GenericCrudController>
                extends AbstractMvcTest<C> {
}
