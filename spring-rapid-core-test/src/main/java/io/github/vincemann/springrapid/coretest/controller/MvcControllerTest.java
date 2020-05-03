package io.github.vincemann.springrapid.coretest.controller;

import io.github.vincemann.springrapid.coretest.InitializingTest;
import io.github.vincemann.springrapid.coretest.automock.AutoMockServiceBeansGenericAnnotationWebConfigContextLoader;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.MockMvcConfigurer;
import org.springframework.test.web.servlet.setup.MockMvcConfigurerAdapter;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

/**
 * BaseClass for Controller Integration tests performing basic {@link MockMvc} auto-config.
 * Does not load {@link io.github.vincemann.springrapid.core.slicing.components.ServiceComponent}s and {@link io.github.vincemann.springrapid.core.slicing.config.ServiceConfig}s.
 * All service beans are automatically mocked by {@link AutoMockServiceBeansGenericAnnotationWebConfigContextLoader}.
 * You can get the mocks by using @{@link org.springframework.beans.factory.annotation.Autowired}.
 *
 * Spring Context is configured for a minimal AutoConfigSetup for webTests.
 */
@Getter
@Setter
@Slf4j
@ActiveProfiles(value = {"test","web","webTest"})
@SpringBootTest
//all service interaction is mocked -> no interaction with database -> no datasource config needed
@ImportAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
@AutoConfigureMockMvc
@ContextConfiguration(
        loader = AutoMockServiceBeansGenericAnnotationWebConfigContextLoader.class/*,
        classes = PropertyPlaceholderAutoConfiguration.class*/,
        initializers = ConfigFileApplicationContextInitializer.class
)
public abstract class MvcControllerTest extends InitializingTest {
    private MockMvc mockMvc;
    private DefaultMockMvcBuilder mockMvcBuilder;
    private MediaType contentType = MediaType.APPLICATION_JSON_UTF8;

    @BeforeEach
    public void setupMvc(WebApplicationContext wac) {
        mockMvcBuilder = MockMvcBuilders.webAppContextSetup(wac)
                //user has to check himself if he wants to
//                .alwaysExpect(content().contentType(getContentType()))
                .alwaysDo(print());
        mockMvc=mockMvcBuilder.build();
    }
}
