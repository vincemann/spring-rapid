package com.github.vincemann.springrapid.core.proxy.annotation;

import com.github.vincemann.springrapid.core.proxy.ServiceExtension;
import com.github.vincemann.springrapid.core.proxy.ExtensionProxy;
import com.github.vincemann.springrapid.core.proxy.ExtensionProxyBuilder;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.util.ContainerAnnotationUtils;
import com.github.vincemann.springrapid.core.util.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.TargetClassAware;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.test.util.AopTestUtils;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * Parses proxy annotations {@link CreateProxy} and {@link DefineProxy} and creates extension proxies for {@link CrudService}s.
 * see {@link ExtensionProxy}.
 */
@Slf4j
public class AnnotationExtensionProxyFactory implements BeanPostProcessor, ApplicationContextAware, Ordered {

    private DefaultListableBeanFactory beanFactory;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.beanFactory = ((DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        //log.debug("postProcessing bean : " + beanName);
        if (bean instanceof CrudService && !(bean instanceof ServiceExtension)) {
            Object proxied = beanFactory.getBean(beanName);
            Object unwrappedBean = AopTestUtils.getUltimateTargetObject(proxied);


            List<DefineProxy> proxyDefinitions = ContainerAnnotationUtils.findAnnotations(unwrappedBean.getClass(), DefineProxy.class, DefineProxies.class);
            List<CreateProxy> toCreate = ContainerAnnotationUtils.findAnnotations(unwrappedBean.getClass(), CreateProxy.class, CreateProxies.class);
            Map<String, Object> createdInternalProxies = new HashMap<>();
            if (toCreate.isEmpty()) {
                return bean;
            }
            boolean primaryBeanRegistered = beanFactory.getBeanDefinition(beanName).isPrimary();

//            Class serviceInterface = resolveServiceInterface(unwrappedBean, beanName);
            // todo fancyly log this annotation to show proxy chain with arrows
            if (log.isDebugEnabled())
                log.debug("Identified Proxies of bean: " + beanName + " : " + toCreate);

            for (CreateProxy proxy : toCreate) {
                // make sure there is only one primary bean
                if (proxy.primary()) {
                    if (primaryBeanRegistered) {
                        throw new IllegalArgumentException("Multiple ProxyBeans marked as primary");
                    }
                    primaryBeanRegistered = true;
                }

                Assert.notEmpty(proxy.qualifiers(), "must at least have one qualifier");

                GenericBeanDefinition proxyBeanDef = createBeanDefinition(proxy.qualifiers(), proxy.primary(), beanName);
                String proxyBeanName = resolveProxyName(proxy.qualifiers(), proxy.name(), unwrappedBean.getClass());
                if (log.isDebugEnabled())
                    log.debug("creating proxyBean with name: " + proxyBeanName);

                // compose proxy instance by creating all internal proxies needed and form a proxy chain
                Object lastProxiedBean = proxied;
                for (String proxyName : proxy.proxies()) {
                    // try to find locally
                    Object internalProxy;
                    Optional<DefineProxy> proxyDefinition = proxyDefinitions
                            .stream()
                            .filter(p -> p.name().equals(proxyName))
                            .findFirst();
                    if (proxyDefinition.isEmpty()) {
                        // did not find matching local proxy definition, must be a proxy from elsewhere
                        // try to find globally, proxy definitions name is assumed to be the global bean name
                        if (beanFactory.containsBean(proxyName)) {
                            internalProxy = beanFactory.getBean(proxyName);
                        } else {
                            throw new IllegalArgumentException("Proxy with name: " + proxyName + " could not be found. Make sure to create a local ProxyDefinition with this name or define a bean globally with that name");
                        }
                    } else {
                        // this is the normal case
                        // found local proxy definition, now create proxy
//                        internalProxy = createdInternalProxies.get(beanName);
                        internalProxy = createdInternalProxies.get(proxyName);
                        boolean defaultEnabled = proxyDefinition.get().defaultExtensionsEnabled();
                        ServiceExtension[] extensions = resolveExtensions(proxyDefinition.get())
                                .toArray(new ServiceExtension[0]);
                        if (internalProxy == null) {
                            internalProxy = new ExtensionProxyBuilder<>(lastProxiedBean)
                                    // todo does this work?
                                    .addExtensions(extensions)
                                    .setDefaultExtensionsEnabled(defaultEnabled)
                                    .ignoreDefaultExtensions(proxyDefinition.get().ignoredExtensions())
                                    .build();
                            createdInternalProxies.put(proxyName, internalProxy);
                        }
                    }
                    lastProxiedBean = internalProxy;
                }
                // the last created proxy from the chain is the most outer proxy -> entry point for proxy chain -> gets autowired
                Object proxyBean = lastProxiedBean;

                if (log.isDebugEnabled()) {
                    log.debug("creating proxyBean : " + proxyBean);
                    log.debug("Registering beanDef of proxyBean first: " + proxyBeanDef);
                }

                // register bean here like securedFooService mapped to qualifier secured for example
                // when this proxy is autowired via @Secured @Autowired bean, then bean will never be wrapped with aop proxy
                // keep that in mind, jdk proxies dont match aop
                // but the most inner proxied bean (the root version of the service) will be wrapped with aop proxy
                beanFactory.registerBeanDefinition(proxyBeanName, proxyBeanDef);
                beanFactory.registerSingleton(proxyBeanName, proxyBean);

                if (log.isDebugEnabled())
                    log.debug("registered proxyBean: " + proxyBeanName);
            }
        }
        return bean;
    }


    protected Class resolveServiceInterface(Object bean, String beanName) {
        // if needed, just take first interface found, or define var in annotation holding main interface
//        String entityName = ((CrudService) bean).getEntityClass().getSimpleName();
//        String interfaceName = entityName + "Service";
//        Optional<Class<?>> serviceInterfaceClass = Lists.newArrayList(bean.getClass().getInterfaces()).stream()
//                .filter(i -> i.getSimpleName().equals(interfaceName))
//                .findFirst();
//        Assert.isTrue(serviceInterfaceClass.isPresent(), "Could not find interface named: " + interfaceName + " for Service bean: " + beanName + " please create interface following namingConvention : 'entityName+Service'");
//        return serviceInterfaceClass.get();
        return null;
    }

    protected GenericBeanDefinition createBeanDefinition(
            Class<? extends Annotation>[] qualifiers,
            boolean primary,
            String targetBeanName) {

        // Retrieve the actual class of the target bean
        Class<?> targetBeanClass = beanFactory.getType(targetBeanName);

        // Ensure the target bean class is available
        Assert.notNull(targetBeanClass, "Target bean class must not be null");

        // Get all interfaces implemented by the target bean class
        Class<?>[] interfaces = targetBeanClass.getInterfaces();

        // Assert that there are interfaces to proxy
        Assert.notEmpty(interfaces, "Target bean must implement at least one interface");

        // Create the bean definition for the ProxyFactoryBean
        GenericBeanDefinition proxyBeanDefinition = new GenericBeanDefinition();
        proxyBeanDefinition.setBeanClass(ProxyFactoryBean.class);
        proxyBeanDefinition.getPropertyValues().add("proxyInterfaces", interfaces);
        proxyBeanDefinition.getPropertyValues().add("targetName", targetBeanName);

        // Manually add qualifiers
        for (Class<? extends Annotation> qualifier : qualifiers) {
            Assert.isTrue(qualifier.isAnnotationPresent(Qualifier.class), "Provided class must be a Qualifier");
            proxyBeanDefinition.addQualifier(new AutowireCandidateQualifier(qualifier));
        }

        proxyBeanDefinition.setPrimary(primary);
        return proxyBeanDefinition;
    }

    protected String resolveProxyName(Class<? extends Annotation>[] qualifiers, String userBeanName, Class beanType) {
        if (!userBeanName.isEmpty())
            return userBeanName;
        // need to create own bean name, user has not supplied one
        StringBuilder sb = new StringBuilder();
        Arrays.stream(qualifiers)
                .forEach(type -> {
                    sb.append(type.getSimpleName());
                    sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
                });
        String prefix = sb.toString();

        return prefix + beanType.getSimpleName();
    }


    protected List<ServiceExtension> resolveExtensions(DefineProxy proxyDefinition) {
        String[] beanNameExtensions = proxyDefinition.extensions();
        Class[] classExtensions = proxyDefinition.extensionClasses();
        if (beanNameExtensions.length > 0 && classExtensions.length > 0)
            throw new IllegalArgumentException("Only use either 'extensions' or 'extensionClasses' in Annotation Proxy");
        if (classExtensions.length > 0) {
            return resolveExtensions(classExtensions);
        } else if (beanNameExtensions.length > 0) {
            return resolveExtensions(beanNameExtensions);
        } else {
            return new ArrayList<>();
        }
    }

    protected List<ServiceExtension> resolveExtensions(Class[] classExtensions) {
        List<ServiceExtension> extensions = new ArrayList<>();
        ArrayList<Class> extensionStrings = Lists.newArrayList(classExtensions);
        for (Class extensionClass : extensionStrings) {
            Object extension = beanFactory.getBean(extensionClass);
            if (!(extension instanceof ServiceExtension)) {
                throw new IllegalArgumentException("Given extension bean: " + extensionClass.getSimpleName() + " is not of Type BasicServiceExtension");
            }
            ServiceExtension serviceExtension = (ServiceExtension) extension;
            beanFactory.autowireBean(extension);
            //beanFactory.autowireBeanProperties();
            extensions.add(serviceExtension);

        }
        return extensions;
    }

    protected List<ServiceExtension> resolveExtensions(String[] extensionStringArr) {
        List<ServiceExtension> extensions = new ArrayList<>();
        ArrayList<String> extensionStrings = Lists.newArrayList(extensionStringArr);
        for (String extensionString : extensionStrings) {
            Object extension = beanFactory.getBean(extensionString);
            if (!(extension instanceof ServiceExtension)) {
                throw new IllegalArgumentException("Given extension bean: " + extensionString + " is not of Type AbstractServiceExtension");
            }
            ServiceExtension serviceExtension = (ServiceExtension) extension;
            beanFactory.autowireBean(extension);
            //beanFactory.autowireBeanProperties();
            extensions.add(serviceExtension);

        }
        return extensions;
    }
}
