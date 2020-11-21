package com.github.vincemann.springrapid.coretest.controller.automock;

import com.github.vincemann.springrapid.core.CoreProperties;
import com.github.vincemann.springrapid.core.slicing.RapidProfiles;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.core.slicing.ServiceConfig;
import com.github.vincemann.springrapid.coretest.InitializingTest;
import com.github.vincemann.springrapid.coretest.controller.automock.AutoMockBeansTestExecutionListener;
import com.github.vincemann.springrapid.coretest.slicing.RapidTestProfiles;
import com.github.vincemann.springrapid.coretest.controller.automock.AutoMockServiceBeansGenericAnnotationWebConfigContextLoader;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

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
 */
@Getter
@Setter
@Slf4j
@ActiveProfiles(value = {RapidTestProfiles.TEST, RapidProfiles.WEB, RapidTestProfiles.WEB_TEST})
@SpringBootTest
@TestExecutionListeners(value = {
        AutoMockBeansTestExecutionListener.class},
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
//complete service interaction is mocked -> no interaction with database -> no datasource config needed
@ImportAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
@AutoConfigureMockMvc
@ContextConfiguration(
        loader = AutoMockServiceBeansGenericAnnotationWebConfigContextLoader.class/*,
        classes = PropertyPlaceholderAutoConfiguration.class*/,
        initializers = ConfigFileApplicationContextInitializer.class
)
public abstract class AutoMockControllerTest extends InitializingTest {
    private MockMvc mockMvc;
    private DefaultMockMvcBuilder mockMvcBuilder;
    private MediaType contentType;

    @Autowired
    private CoreProperties coreProperties;

    @BeforeEach
    protected void setupMvc(WebApplicationContext wac) {
        this.contentType = MediaType.valueOf(coreProperties.getController().getMediaType());
        mockMvcBuilder = MockMvcBuilders.webAppContextSetup(wac)
                //user has to check himself if he wants to
//                .alwaysExpect(content().contentType(getContentType()))
                .alwaysDo(print());
        mockMvc=mockMvcBuilder.build();
    }
}
