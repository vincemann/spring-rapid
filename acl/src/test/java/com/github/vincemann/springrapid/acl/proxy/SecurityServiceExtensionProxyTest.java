package com.github.vincemann.springrapid.acl.proxy;

import com.github.vincemann.springrapid.core.util.Lists;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import com.github.vincemann.springrapid.core.proxy.CrudServiceExtension;
import com.github.vincemann.springrapid.core.service.AbstractCrudService;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.JPACrudService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.internal.InOrderImpl;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.TargetClassAware;
import org.springframework.data.jpa.repository.JpaRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class SecurityServiceExtensionProxyTest {

    Service proxy;
    @Spy
    Service service;
    @Spy
    DefaultSecurityExtension defaultSecurityExtension;
    @Spy
    FooSecurityExtension fooSecurityExtension;
    @Mock
    Entity entity;

//    @Mock
//    ApplicationContext applicationContext;

    @BeforeEach
    void setUp() {
//        Mockito.when(applicationContext.getBean(eq("defaultServiceSecurityRule")))
//                .thenReturn(defaultSecurityExtension);
        proxy = new SecurityServiceExtensionProxyBuilder<>(service, defaultSecurityExtension)
                .addExtensions(fooSecurityExtension)
                .build();
//        SecurityServiceExtensionProxyBuilderFactory factory = new SecurityServiceExtensionProxyBuilderFactory();
//        factory.setApplicationContext(applicationContext);
//        proxy = factory
//                .create(service)
//                .addExtensions(fooSecurityExtension)
//                .build();
    }

    @Test
    public void testCallDefaultRule() throws BadEntityException {
        Long id = 42L;
        InOrder inOrder = new InOrderImpl(Lists.newArrayList(service, defaultSecurityExtension, fooSecurityExtension));
        proxy.findById(id);
        inOrder.verify(fooSecurityExtension).findById(id);
        inOrder.verify(defaultSecurityExtension).findById(id);
        inOrder.verify(service).findById(id);
    }

    @Test
    public void testOverrideDefaultRule() throws BadEntityException {
        InOrder inOrder = new InOrderImpl(Lists.newArrayList(service, fooSecurityExtension));
        proxy.save(entity);
        inOrder.verify(fooSecurityExtension).save(entity);
        inOrder.verify(service).save(entity);

        Mockito.verify(defaultSecurityExtension, Mockito.never()).save(any());
    }

    @Test
    public void testOverrideDefaultRule_callNonOverridingMethod() throws BadEntityException {
        testOverrideDefaultRule();
        testCallDefaultRule();
    }

    interface Service extends CrudService<Entity, Long> {
    }

    @AllArgsConstructor
    @NoArgsConstructor
    class ExampleEntity extends IdentifiableEntityImpl<Long> {
        String name;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    class Entity extends IdentifiableEntityImpl<Long> {
        String name;
    }

    class ServiceImpl extends JPACrudService<Entity, Long, JpaRepository<Entity, Long>>
            implements Service, TargetClassAware {

        @Override
        public Class<?> getTargetClass() {
            return ServiceImpl.class;
        }
    }

    class FooSecurityExtension extends SecurityServiceExtension<CrudService> implements CrudServiceExtension<CrudService> {

        @Override
        public IdentifiableEntity save(IdentifiableEntity entity) throws BadEntityException {
            getProxyController().overrideDefaultExtension();
            return getNext().save(entity);
        }

    }

    class DefaultSecurityExtension extends SecurityServiceExtension<CrudService> implements CrudServiceExtension<CrudService> {


    }
}