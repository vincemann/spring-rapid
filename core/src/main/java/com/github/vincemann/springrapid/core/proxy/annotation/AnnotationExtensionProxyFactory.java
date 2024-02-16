package com.github.vincemann.springrapid.core.proxy.annotation;

import com.github.vincemann.springrapid.core.proxy.ServiceExtension;
import com.github.vincemann.springrapid.core.proxy.ExtensionProxy;
import com.github.vincemann.springrapid.core.proxy.ExtensionProxyBuilder;
import com.github.vincemann.springrapid.core.util.ContainerAnnotationUtils;
import com.github.vincemann.springrapid.core.util.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Parses {@link DefineProxy} and creates and stores respective Extension Proxies.
 * Parses {@link CreateProxy} and creates respective proxy chains and publishes them to context.
 * Parses {@link AutowireProxy} annotations and inject respective proxy chains into fields or via setter injection (constructor injection is not supported).
 * see {@link ExtensionProxy}.
 */
@Slf4j
public class AnnotationExtensionProxyFactory implements BeanPostProcessor, ApplicationContextAware, Ordered {

    private DefaultListableBeanFactory beanFactory;

    // stores mapping of proxied class to proxy info (all proxy definitions like "acl","secured" stubs + root proxied obj)
    // with this info proxy chains can be created
    private Map<Class<?>, ProxyInfo> proxyInfos = new HashMap<>();


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

        if (! (bean instanceof ServiceExtension)){
            Object unwrappedBean = unwrap(bean);

            List<DefineProxy> proxyDefinitions = ContainerAnnotationUtils.findAnnotations(unwrappedBean.getClass(), DefineProxy.class, DefineProxies.class);
            saveProxyInfo(beanName, proxyDefinitions);


            List<CreateProxy> toCreate = ContainerAnnotationUtils.findAnnotations(unwrappedBean.getClass(), CreateProxy.class, CreateProxies.class);
            createProxies(beanName, toCreate);
        }


        autowireProxyChains(bean);
        return bean;
    }

    private void autowireProxyChains(Object bean) {
        Class<?> targetClass = bean.getClass();

        // Handle field injection
        ReflectionUtils.doWithFields(targetClass, field -> {
            AutowireProxy proxyChain = field.getAnnotation(AutowireProxy.class);
            if (proxyChain != null) {
                field.setAccessible(true);
                Object proxiedBean = createProxyChain(field.getType(), proxyChain.value());
                field.set(bean, proxiedBean);
            }
        });

        // Handle setter injection
        ReflectionUtils.doWithMethods(targetClass, method -> {
            if (method.getParameterCount() == 1 && method.isAnnotationPresent(AutowireProxy.class)) {
                AutowireProxy proxyChain = method.getAnnotation(AutowireProxy.class);
                method.setAccessible(true);
                Object proxiedBean = createProxyChain(method.getParameterTypes()[0], proxyChain.value());
                try {
                    method.invoke(bean, proxiedBean);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }


    protected void createProxies(String beanName, List<CreateProxy> toCreate) {
        if (toCreate.isEmpty())
            return;
        // this will make sure root proxied bean is wrapped with aop proxy
        Object proxied = beanFactory.getBean(beanName);
        Object unwrappedBean = unwrap(proxied);

        boolean primaryBeanRegistered = beanFactory.getBeanDefinition(beanName).isPrimary();

        if (log.isDebugEnabled())
            log.debug("Identified Proxies of bean: " + beanName + " : " + toCreate);

        for (CreateProxy proxy : toCreate) {
            // make sure there is only one primary bean
            if (proxy.primary()) {
                Assert.state(!primaryBeanRegistered, "Multiple ProxyBeans marked as primary");
                primaryBeanRegistered = true;
            }

            Assert.notEmpty(proxy.qualifiers(), "must at least have one qualifier");


            // compose proxy instance by creating all internal proxies needed and form a proxy chain

            String proxyBeanName = proxy.name().isEmpty()
                    ?
                    resolveProxyName(proxy.qualifiers(), unwrappedBean.getClass())
                    :
                    proxy.name();

            if (log.isDebugEnabled())
                log.debug("creating proxyBean with name: " + proxyBeanName);


            // the last created proxy from the chain is the most outer proxy -> entry point for proxy chain -> gets autowired
            Object proxyBean = createProxyChain(unwrappedBean.getClass(), proxy.proxies());

            if (log.isDebugEnabled()) {
                log.debug("registering proxy as bean : " + proxyBean);
            }

            // register bean here like securedFooService mapped to qualifier secured for example
            // when this proxy is autowired via @Secured @Autowired bean, then bean will never be wrapped with aop proxy
            // keep that in mind, jdk proxies dont match aop
            // but the most inner proxied bean (the root version of the service) will be wrapped with aop proxy
            registerBean(proxied,proxy.qualifiers(), proxy.primary(), proxyBean, proxyBeanName);

            if (log.isDebugEnabled())
                log.debug("registered proxy as bean: " + proxyBeanName);
        }
    }

    protected Object createProxyChain(Object root, String[] proxies){
        ProxyInfo proxyInfo = findProxyInfo(unwrap(root).getClass());

        Object lastProxiedBean = root;
        for (String proxyName : proxies) {
            // try to find locally
            Optional<DefineProxy> proxyDefinition = findProxyDefinition(proxyInfo, proxyName);
            if (proxyDefinition.isEmpty()) {
                // did not find matching local proxy definition, must be a proxy from elsewhere
                // try to find globally, proxy definitions name is assumed to be the global bean name
                Assert.state(beanFactory.containsBean(proxyName), "Proxy with name: " + proxyName + " could not be found. Make sure to create a local ProxyDefinition with this name or define a bean globally with that name");
                lastProxiedBean = beanFactory.getBean(proxyName);
            } else {
                // this is the normal case
                // found local proxy definition, now create proxy
                lastProxiedBean = createProxy(lastProxiedBean, proxyDefinition.get());
            }
        }
        return lastProxiedBean;
    }

    protected Object createProxyChain(Class<?> proxiedClass, String[] proxies) {
        ProxyInfo proxyInfo = findProxyInfo(proxiedClass);

        Object root = proxyInfo.getRootProxied();
        return createProxyChain(root,proxies);
    }

    protected Optional<DefineProxy> findProxyDefinition(ProxyInfo info, String proxyName) {
        return info.getProxyDefinitions().stream()
                .filter(p -> p.name().equals(proxyName))
                .findFirst();
    }

    protected ProxyInfo findProxyInfo(Class<?> proxiedClass) {
        return proxyInfos.get(proxiedClass);
    }

    protected Object unwrap(Object bean) {
        Object unwrappedBean = AopProxyUtils.getSingletonTarget(bean);
        if (unwrappedBean == null)
            return bean;
        return unwrappedBean;
    }

    protected void saveProxyInfo(String beanName, List<DefineProxy> proxyDefinitions) {
        if (proxyDefinitions.isEmpty())
            return;
        Object proxied = beanFactory.getBean(beanName);
        Class<?> proxiedClass = unwrap(proxied).getClass();
        for (DefineProxy proxyDefinition : proxyDefinitions) {
            Assert.isTrue(!proxyDefinition.name().isEmpty(), "must provide name for proxy");
        }
        // this will make sure root proxied bean is wrapped with aop proxy

        this.proxyInfos.put(proxiedClass, new ProxyInfo(proxied, proxyDefinitions));
    }

    @AllArgsConstructor
    @Getter
    private static class ProxyInfo {
        private Object rootProxied;
        private List<DefineProxy> proxyDefinitions;
    }

    protected <T> T createProxy(T proxied, DefineProxy proxyDefinition) {
        Assert.isTrue(!proxyDefinition.name().isEmpty(), "must provide name for proxy");
        boolean defaultExtensions = proxyDefinition.defaultExtensionsEnabled();
        ServiceExtension[] extensions = resolveExtensions(proxyDefinition)
                .toArray(new ServiceExtension[0]);

        return (T) new ExtensionProxyBuilder<>(proxied)
                .addExtensions(extensions)
                .defaultExtensionsEnabled(defaultExtensions)
                .ignoreDefaultExtensions(proxyDefinition.ignoredExtensions())
                .build();

    }


    protected void registerBean(
            Object proxied,
            Class<? extends Annotation>[] qualifiers,
            boolean primary,
            Object proxy,
            String proxyBeanName
    ) {


        Class<?>[] interfaces = unwrap(proxied).getClass().getInterfaces();

        // Assert that there are interfaces to proxy
        Assert.notEmpty(interfaces, "Target bean must implement at least one interface");

        GenericBeanDefinition proxyBeanDefinition = new GenericBeanDefinition();
        // Dealing with a jdk proxy here, no class known -> bean just implements interfaces - will be autowired by interface anyways
        proxyBeanDefinition.setBeanClass(null);

        // Manually add qualifiers
        for (Class<? extends Annotation> qualifierContainer : qualifiers) {
            // needs to be done like this
            Qualifier qualifier = AnnotationUtils.findAnnotation(qualifierContainer, Qualifier.class);
            Assert.notNull(qualifier, "Provided annotation class must have a meta qualifier annotation");
            proxyBeanDefinition.addQualifier(new AutowireCandidateQualifier(Qualifier.class, qualifier.value()));
        }

        proxyBeanDefinition.setPrimary(primary);

        // Register the proxy bean definition with the application context
        beanFactory.registerBeanDefinition(proxyBeanName, proxyBeanDefinition);
        beanFactory.registerSingleton(proxyBeanName, proxy);
    }


    protected String resolveProxyName(Class<? extends Annotation>[] qualifiers, Class beanType) {
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
        boolean nameAndClassesSet = beanNameExtensions.length > 0 && classExtensions.length > 0;
        Assert.state(!nameAndClassesSet, "Only use either 'extensions' or 'extensionClasses' in annotation based proxy creation");
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
            assertPrototype(extensionClass);
            Object extension = beanFactory.getBean(extensionClass);
            Assert.isInstanceOf(ServiceExtension.class, extension, "Given extension bean: " + extension.getClass() + " must be of type ServiceExtension");
            ServiceExtension serviceExtension = (ServiceExtension) extension;
            beanFactory.autowireBean(extension);
            //beanFactory.autowireBeanProperties();
            extensions.add(serviceExtension);

        }
        return extensions;
    }

    protected List<ServiceExtension> resolveExtensions(String[] extensionBeanNames) {
        List<ServiceExtension> extensions = new ArrayList<>();
        ArrayList<String> extensionNames = Lists.newArrayList(extensionBeanNames);
        for (String extensionName : extensionNames) {

            assertPrototype(extensionName);
            Object extension = beanFactory.getBean(extensionName);
            Assert.isInstanceOf(ServiceExtension.class, extension, "Given extension bean: " + extension.getClass() + " must be of type ServiceExtension");
            ServiceExtension serviceExtension = (ServiceExtension) extension;
            beanFactory.autowireBean(extension);
            //beanFactory.autowireBeanProperties();
            extensions.add(serviceExtension);

        }
        return extensions;
    }

    protected void assertPrototype(String beanName){
        BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
        Assert.state(beanDefinition.isPrototype(),"extensions must be of scope prototype");
    }

    protected void assertPrototype(Class<?> clazz){
        String[] beanNamesForType = beanFactory.getBeanNamesForType(clazz);
        Assert.isTrue(beanNamesForType.length == 1,"must find at least one bean for extension class: " + clazz);
        String beanName = beanNamesForType[0];
        assertPrototype(beanName);
    }
}
