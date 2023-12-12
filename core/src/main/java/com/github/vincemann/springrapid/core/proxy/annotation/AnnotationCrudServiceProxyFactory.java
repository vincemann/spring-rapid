package com.github.vincemann.springrapid.core.proxy.annotation;

import com.github.vincemann.springrapid.core.proxy.AbstractServiceExtension;
import com.github.vincemann.springrapid.core.proxy.ServiceExtensionProxyBuilder;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.util.ContainerAnnotationUtils;
import com.github.vincemann.springrapid.core.util.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.util.AopTestUtils;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.util.*;

@Slf4j
public class AnnotationCrudServiceProxyFactory implements BeanPostProcessor, ApplicationContextAware {

    private DefaultListableBeanFactory beanFactory;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.beanFactory = ((DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory());
    }


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        //log.debug("postProcessing bean : " + beanName);
        Object unwrappedBean = AopTestUtils.getUltimateTargetObject(bean);
        if (unwrappedBean instanceof CrudService && !(unwrappedBean instanceof AbstractServiceExtension)) {
            List<DefineProxy> proxyDefinitions = ContainerAnnotationUtils.findAnnotations(unwrappedBean.getClass(), DefineProxy.class, DefineProxies.class);
            List<CreateProxy> toCreate = ContainerAnnotationUtils.findAnnotations(unwrappedBean.getClass(), CreateProxy.class, CreateProxies.class);
            Map<String, CrudService> createdInternalProxies = new HashMap<>();
            if (toCreate.isEmpty()) {
                return bean;
            }
            boolean primaryBeanRegistered = beanFactory.getBeanDefinition(beanName).isPrimary();
            Class serviceInterface = resolveServiceInterface(unwrappedBean, beanName);
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

                GenericBeanDefinition proxyBeanDef
                        = createBeanDef(proxy.qualifiers(), proxy.primary(), ((Class<? extends CrudService>) serviceInterface));
                String proxyBeanName = resolveProxyName(proxy.qualifiers(), proxy.primary(), proxy.name(), unwrappedBean.getClass());
                if (log.isDebugEnabled())
                    log.debug("creating proxyBean with name: " + proxyBeanName);

                // compose proxy instance by creating all internal proxies needed and form a proxy chain
                CrudService lastProxiedBean = (CrudService) bean;
                for (String proxyName : proxy.proxies()) {
                    // try to find locally
                    CrudService internalProxy;
                    Optional<DefineProxy> proxyDefinition = proxyDefinitions
                            .stream()
                            .filter(p -> p.name().equals(proxyName))
                            .findFirst();
                    if (proxyDefinition.isEmpty()) {
                        // did not find matching local proxy definition, must be a proxy from elsewhere
                        // try to find globally, proxy definitions name is assumed to be the global bean name
                        if (beanFactory.containsBean(proxyName)) {
                            internalProxy = (CrudService) beanFactory.getBean(proxyName);
                        } else {
                            throw new IllegalArgumentException("Proxy with name: " + proxyName + " could not be found. Make sure to create a local ProxyDefinition with this name or define a bean globally with that name");
                        }
                    } else {
                        // this is the normal case
                        // found local proxy definition, now create proxy
//                        internalProxy = createdInternalProxies.get(beanName);
                        internalProxy = createdInternalProxies.get(proxyName);
                        boolean defaultEnabled = proxyDefinition.get().defaultExtensionsEnabled();
                        if (internalProxy == null) {
                            internalProxy = new ServiceExtensionProxyBuilder<>(lastProxiedBean)
                                    .addGenericExtensions(
                                            resolveExtensions(proxyDefinition.get().extensions())
                                                    .toArray(new AbstractServiceExtension[0])
                                    )
                                    .toggleDefaultExtensions(defaultEnabled)
                                    .ignoreDefaultExtensions(proxyDefinition.get().ignoredExtensions())
                                    .build();
                            createdInternalProxies.put(proxyName, internalProxy);
                        }
                    }
                    lastProxiedBean = internalProxy;
                }
                // the last created proxy from the chain is the most outer proxy -> entry point for proxy chain -> gets autowired
                CrudService proxyBean = lastProxiedBean;

                if (log.isDebugEnabled()){
                    log.debug("creating proxyBean : " + proxyBean);
                    log.debug("Registering beanDef of proxyBean first: " + proxyBeanDef);
                }
                beanFactory.registerBeanDefinition(proxyBeanName, proxyBeanDef);
                beanFactory.registerSingleton(proxyBeanName, proxyBean);
                proxyBean.setBeanName(beanName);
                if (log.isDebugEnabled())
                    log.debug("registered proxyBean: " + proxyBeanName);
            }
        }
        return bean;
    }


    protected Class resolveServiceInterface(Object bean, String beanName) {
        String entityName = ((CrudService) bean).getEntityClass().getSimpleName();
        String interfaceName = entityName + "Service";
        Optional<Class<?>> serviceInterfaceClass = Lists.newArrayList(bean.getClass().getInterfaces()).stream()
                .filter(i -> i.getSimpleName().equals(interfaceName))
                .findFirst();
        Assert.isTrue(serviceInterfaceClass.isPresent(), "Could not find interface named: " + interfaceName + " for Service bean: " + beanName + " please create interface following namingConvention : 'entityName+Service'");
        return serviceInterfaceClass.get();
    }

    protected GenericBeanDefinition createBeanDef(Class<? extends Annotation>[] qualifiers, boolean primary, Class<? extends CrudService> beanClass) {
        final GenericBeanDefinition serviceBeanDef = new GenericBeanDefinition();
        for (Class<? extends Annotation> qualifier : qualifiers) {
            Assert.isTrue(qualifier.isAnnotationPresent(Qualifier.class));
            serviceBeanDef.addQualifier(new AutowireCandidateQualifier(qualifier));
        }
        serviceBeanDef.setPrimary(primary);
        serviceBeanDef.setBeanClass(beanClass);
        return serviceBeanDef;
    }

    protected String resolveProxyName(Class<? extends Annotation>[] qualifiers, boolean primary, String beanName, Class beanType) {
        String name = beanName;
        if (name.isEmpty()) {
            String prefix;
            if (primary) {
                prefix = "primary";
            } else {
                StringBuilder sb = new StringBuilder();
                Arrays.stream(qualifiers)
                        .forEach(type -> {
                            sb.append(type.getSimpleName());
                            sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
                        });
                prefix = sb.toString();
            }
            return prefix + beanType.getSimpleName();
        }
        return name;
    }

    protected List<AbstractServiceExtension> resolveExtensions(String[] extensionStringArr) {
        List<AbstractServiceExtension> extensions = new ArrayList<>();
        ArrayList<String> extensionStrings = Lists.newArrayList(extensionStringArr);
        for (String extensionString : extensionStrings) {
            Object extension = null;
//            if (extensionString.endsWith(".class")) {
//                try {
//                    Class<?> extensionType = Class.forName(extensionString);
//                    extension = beanFactory.getBean(extensionType);
//                } catch (ClassNotFoundException e) {
//                    throw new IllegalArgumentException("Could not find matching class for extension: " + extensionString, e);
//                }
//            } else {
            extension = beanFactory.getBean(extensionString);
//            }
            if (!(extension instanceof AbstractServiceExtension)) {
                throw new IllegalArgumentException("Given extension bean: " + extensionString + " is not of Type AbstractServiceExtension");
            }
            AbstractServiceExtension serviceExtension = (AbstractServiceExtension) extension;
            beanFactory.autowireBean(extension);
            //beanFactory.autowireBeanProperties();
            extensions.add(serviceExtension);

        }
        return extensions;
    }


//    private List<SecurityServiceExtension> resolveRules(SecurityProxy proxy){
//        List<SecurityServiceExtension> rules = new ArrayList<>();
//        ArrayList<Class<? extends SecurityServiceExtension>> pluginTypes = Lists.newArrayList(proxy.rules());
//        for (Class<? extends SecurityServiceExtension> ruleType : pluginTypes) {
//            SecurityServiceExtension rule = beanFactory.getBean(ruleType);
//            rules.add(rule);
//        }
//        return rules;
//    }
}
