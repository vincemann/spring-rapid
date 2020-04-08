package io.github.vincemann.springrapid.core.test.automock;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.support.AbstractContextLoader;
import org.springframework.test.context.support.AnnotationConfigContextLoaderUtils;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebMergedContextConfiguration;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;

import javax.servlet.ServletContext;

/**
 * copied version with one change according to https://stackoverflow.com/questions/49124887/how-to-mock-absent-bean-definitions-in-springjunit4classrunner/49220187#49220187
 */
public class AutoMockServiceBeansGenericAnnotationWebConfigContextLoader extends AbstractContextLoader{


    private static final Log logger = LogFactory.getLog(AnnotationConfigWebContextLoader.class);


    // SmartContextLoader

    /**
     * Process <em>annotated classes</em> in the supplied {@link ContextConfigurationAttributes}.
     * <p>If the <em>annotated classes</em> are {@code null} or empty and
     * {@link #isGenerateDefaultLocations()} returns {@code true}, this
     * {@code SmartContextLoader} will attempt to {@linkplain
     * #detectDefaultConfigurationClasses detect default configuration classes}.
     * If defaults are detected they will be
     * {@linkplain ContextConfigurationAttributes#setClasses(Class[]) set} in the
     * supplied configuration attributes. Otherwise, properties in the supplied
     * configuration attributes will not be modified.
     * @param configAttributes the context configuration attributes to process
     * @see org.springframework.test.context.SmartContextLoader#processContextConfiguration(ContextConfigurationAttributes)
     * @see #isGenerateDefaultLocations()
     * @see #detectDefaultConfigurationClasses(Class)
     */
    @Override
    public void processContextConfiguration(ContextConfigurationAttributes configAttributes) {
        if (!configAttributes.hasClasses() && isGenerateDefaultLocations()) {
            configAttributes.setClasses(detectDefaultConfigurationClasses(configAttributes.getDeclaringClass()));
        }
    }

    /**
     * Detect the default configuration classes for the supplied test class.
     * <p>The default implementation simply delegates to
     * {@link AnnotationConfigContextLoaderUtils#detectDefaultConfigurationClasses(Class)}.
     * @param declaringClass the test class that declared {@code @ContextConfiguration}
     * @return an array of default configuration classes, potentially empty but never {@code null}
     * @see AnnotationConfigContextLoaderUtils
     */
    protected Class<?>[] detectDefaultConfigurationClasses(Class<?> declaringClass) {
        return AnnotationConfigContextLoaderUtils.detectDefaultConfigurationClasses(declaringClass);
    }


    // AbstractContextLoader

    /**
     * {@code AnnotationConfigWebContextLoader} should be used as a
     * {@link org.springframework.test.context.SmartContextLoader SmartContextLoader},
     * not as a legacy {@link org.springframework.test.context.ContextLoader ContextLoader}.
     * Consequently, this method is not supported.
     * @throws UnsupportedOperationException in this implementation
     * @see org.springframework.test.context.support.AbstractContextLoader#modifyLocations
     */
    @Override
    protected String[] modifyLocations(Class<?> clazz, String... locations) {
        throw new UnsupportedOperationException(
                "AnnotationConfigWebContextLoader does not support the modifyLocations(Class, String...) method");
    }

    /**
     * {@code AnnotationConfigWebContextLoader} should be used as a
     * {@link org.springframework.test.context.SmartContextLoader SmartContextLoader},
     * not as a legacy {@link org.springframework.test.context.ContextLoader ContextLoader}.
     * Consequently, this method is not supported.
     * @throws UnsupportedOperationException in this implementation
     * @see org.springframework.test.context.support.AbstractContextLoader#generateDefaultLocations
     */
    @Override
    protected String[] generateDefaultLocations(Class<?> clazz) {
        throw new UnsupportedOperationException(
                "AnnotationConfigWebContextLoader does not support the generateDefaultLocations(Class) method");
    }

    /**
     * {@code AnnotationConfigWebContextLoader} should be used as a
     * {@link org.springframework.test.context.SmartContextLoader SmartContextLoader},
     * not as a legacy {@link org.springframework.test.context.ContextLoader ContextLoader}.
     * Consequently, this method is not supported.
     * @throws UnsupportedOperationException in this implementation
     * @see org.springframework.test.context.support.AbstractContextLoader#getResourceSuffix
     */
    @Override
    protected String getResourceSuffix() {
        throw new UnsupportedOperationException(
                "AnnotationConfigWebContextLoader does not support the getResourceSuffix() method");
    }


    // AbstractGenericWebContextLoader

    /**
     * Register classes in the supplied {@linkplain GenericWebApplicationContext context}
     * from the classes in the supplied {@link WebMergedContextConfiguration}.
     * <p>Each class must represent an <em>annotated class</em>. An
     * {@link AnnotatedBeanDefinitionReader} is used to register the appropriate
     * bean definitions.
     * @param context the context in which the annotated classes should be registered
     * @param webMergedConfig the merged configuration from which the classes should be retrieved
     * @see this#loadBeanDefinitions
     */
    protected void loadBeanDefinitions(
            GenericWebApplicationContext context, WebMergedContextConfiguration webMergedConfig) {

        Class<?>[] annotatedClasses = webMergedConfig.getClasses();
        if (logger.isDebugEnabled()) {
            logger.debug("Registering annotated classes: " + ObjectUtils.nullSafeToString(annotatedClasses));
        }
        new AnnotatedBeanDefinitionReader(context).register(annotatedClasses);
    }

    /**
     * Ensure that the supplied {@link WebMergedContextConfiguration} does not
     * contain {@link MergedContextConfiguration#getLocations() locations}.
     * @since 4.0.4
     * @see this#validateMergedContextConfiguration
     */
    protected void validateMergedContextConfiguration(WebMergedContextConfiguration webMergedConfig) {
        if (webMergedConfig.hasLocations()) {
            String msg = String.format("Test class [%s] has been configured with @ContextConfiguration's 'locations' " +
                            "(or 'value') attribute %s, but %s does not support resource locations.",
                    webMergedConfig.getTestClass().getName(),
                    ObjectUtils.nullSafeToString(webMergedConfig.getLocations()), getClass().getSimpleName());
            logger.error(msg);
            throw new IllegalStateException(msg);
        }
    }






// SmartContextLoader

    /**
     * Load a Spring {@link WebApplicationContext} from the supplied
     * {@link MergedContextConfiguration}.
     * <p>Implementation details:
     * <ul>
     * <li>Calls {@link #validateMergedContextConfiguration(WebMergedContextConfiguration)}
     * to allow subclasses to validate the supplied configuration before proceeding.</li>
     * <li>Creates a {@link GenericWebApplicationContext} instance.</li>
     * <li>If the supplied {@code MergedContextConfiguration} references a
     * {@linkplain MergedContextConfiguration#getParent() parent configuration},
     * the corresponding {@link MergedContextConfiguration#getParentApplicationContext()
     * ApplicationContext} will be retrieved and
     * {@linkplain GenericWebApplicationContext#setParent(ApplicationContext) set as the parent}
     * for the context created by this method.</li>
     * <li>Delegates to {@link #configureWebResources} to create the
     * {@link MockServletContext} and set it in the {@code WebApplicationContext}.</li>
     * <li>Calls {@link #prepareContext} to allow for customizing the context
     * before bean definitions are loaded.</li>
     * <li>Calls {@link #customizeBeanFactory} to allow for customizing the
     * context's {@code DefaultListableBeanFactory}.</li>
     * <li>Delegates to {@link #loadBeanDefinitions} to populate the context
     * from the locations or classes in the supplied {@code MergedContextConfiguration}.</li>
     * <li>Delegates to {@link AnnotationConfigUtils} for
     * {@linkplain AnnotationConfigUtils#registerAnnotationConfigProcessors registering}
     * annotation configuration processors.</li>
     * <li>Calls {@link #customizeContext} to allow for customizing the context
     * before it is refreshed.</li>
     * <li>{@link ConfigurableApplicationContext#refresh Refreshes} the
     * context and registers a JVM shutdown hook for it.</li>
     * </ul>
     * @return a new web application context
     * @see org.springframework.test.context.SmartContextLoader#loadContext(MergedContextConfiguration)
     * @see GenericWebApplicationContext
     */
    @Override
    public final ConfigurableApplicationContext loadContext(MergedContextConfiguration mergedConfig) throws Exception {
        Assert.isTrue(mergedConfig instanceof WebMergedContextConfiguration,
                () -> String.format("Cannot load WebApplicationContext from non-web merged context configuration %s. " +
                        "Consider annotating your test class with @WebAppConfiguration.", mergedConfig));

        WebMergedContextConfiguration webMergedConfig = (WebMergedContextConfiguration) mergedConfig;

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Loading WebApplicationContext for merged context configuration %s.",
                    webMergedConfig));
        }

        validateMergedContextConfiguration(webMergedConfig);

        GenericWebApplicationContext context = new GenericWebApplicationContext(new AutoMockBeanFactory());

        ApplicationContext parent = mergedConfig.getParentApplicationContext();
        if (parent != null) {
            context.setParent(parent);
        }
        configureWebResources(context, webMergedConfig);
        prepareContext(context, webMergedConfig);
        customizeBeanFactory(context.getDefaultListableBeanFactory(), webMergedConfig);
        loadBeanDefinitions(context, webMergedConfig);
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
        customizeContext(context, webMergedConfig);
        context.refresh();
        context.registerShutdownHook();
        return context;
    }


    /**
     * Configures web resources for the supplied web application context (WAC).
     * <h4>Implementation Details</h4>
     * <p>If the supplied WAC has no parent or its parent is not a WAC, the
     * supplied WAC will be configured as the Root WAC (see "<em>Root WAC
     * Configuration</em>" below).
     * <p>Otherwise the context hierarchy of the supplied WAC will be traversed
     * to find the top-most WAC (i.e., the root); and the {@link ServletContext}
     * of the Root WAC will be set as the {@code ServletContext} for the supplied
     * WAC.
     * <h4>Root WAC Configuration</h4>
     * <ul>
     * <li>The resource base path is retrieved from the supplied
     * {@code WebMergedContextConfiguration}.</li>
     * <li>A {@link ResourceLoader} is instantiated for the {@link MockServletContext}:
     * if the resource base path is prefixed with "{@code classpath:}", a
     * {@link DefaultResourceLoader} will be used; otherwise, a
     * {@link FileSystemResourceLoader} will be used.</li>
     * <li>A {@code MockServletContext} will be created using the resource base
     * path and resource loader.</li>
     * <li>The supplied {@link GenericWebApplicationContext} is then stored in
     * the {@code MockServletContext} under the
     * {@link WebApplicationContext#ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE} key.</li>
     * <li>Finally, the {@code MockServletContext} is set in the
     * {@code WebApplicationContext}.</li>
     * </ul>
     * @param context the web application context for which to configure the web resources
     * @param webMergedConfig the merged context configuration to use to load the web application context
     */
    protected void configureWebResources(GenericWebApplicationContext context,
                                         WebMergedContextConfiguration webMergedConfig) {

        ApplicationContext parent = context.getParent();

        // If the WebApplicationContext has no parent or the parent is not a WebApplicationContext,
        // set the current context as the root WebApplicationContext:
        if (parent == null || (!(parent instanceof WebApplicationContext))) {
            String resourceBasePath = webMergedConfig.getResourceBasePath();
            ResourceLoader resourceLoader = (resourceBasePath.startsWith(ResourceLoader.CLASSPATH_URL_PREFIX) ?
                    new DefaultResourceLoader() : new FileSystemResourceLoader());
            ServletContext servletContext = new MockServletContext(resourceBasePath, resourceLoader);
            servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, context);
            context.setServletContext(servletContext);
        }
        else {
            ServletContext servletContext = null;
            // Find the root WebApplicationContext
            while (parent != null) {
                if (parent instanceof WebApplicationContext && !(parent.getParent() instanceof WebApplicationContext)) {
                    servletContext = ((WebApplicationContext) parent).getServletContext();
                    break;
                }
                parent = parent.getParent();
            }
            Assert.state(servletContext != null, "Failed to find root WebApplicationContext in the context hierarchy");
            context.setServletContext(servletContext);
        }
    }

    /**
     * Customize the internal bean factory of the {@code WebApplicationContext}
     * created by this context loader.
     * <p>The default implementation is empty but can be overridden in subclasses
     * to customize {@code DefaultListableBeanFactory}'s standard settings.
     * @param beanFactory the bean factory created by this context loader
     * @param webMergedConfig the merged context configuration to use to load the
     * web application context
     * @see #loadContext(MergedContextConfiguration)
     * @see DefaultListableBeanFactory#setAllowBeanDefinitionOverriding
     * @see DefaultListableBeanFactory#setAllowEagerClassLoading
     * @see DefaultListableBeanFactory#setAllowCircularReferences
     * @see DefaultListableBeanFactory#setAllowRawInjectionDespiteWrapping
     */
    protected void customizeBeanFactory(
            DefaultListableBeanFactory beanFactory, WebMergedContextConfiguration webMergedConfig) {
    }

    /**
     * Customize the {@link GenericWebApplicationContext} created by this context
     * loader <i>after</i> bean definitions have been loaded into the context but
     * <i>before</i> the context is refreshed.
     * <p>The default implementation simply delegates to
     * {@link AbstractContextLoader#customizeContext(ConfigurableApplicationContext, MergedContextConfiguration)}.
     * @param context the newly created web application context
     * @param webMergedConfig the merged context configuration to use to load the
     * web application context
     * @see #loadContext(MergedContextConfiguration)
     * @see #customizeContext(ConfigurableApplicationContext, MergedContextConfiguration)
     */
    protected void customizeContext(
            GenericWebApplicationContext context, WebMergedContextConfiguration webMergedConfig) {

        super.customizeContext(context, webMergedConfig);
    }


    // ContextLoader

    /**
     * {@code AbstractGenericWebContextLoader} should be used as a
     * {@link org.springframework.test.context.SmartContextLoader SmartContextLoader},
     * not as a legacy {@link org.springframework.test.context.ContextLoader ContextLoader}.
     * Consequently, this method is not supported.
     * @see org.springframework.test.context.ContextLoader#loadContext(java.lang.String[])
     * @throws UnsupportedOperationException in this implementation
     */
    @Override
    public final ApplicationContext loadContext(String... locations) throws Exception {
        throw new UnsupportedOperationException(
                "AbstractGenericWebContextLoader does not support the loadContext(String... locations) method");
    }

}
