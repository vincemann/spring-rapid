package com.github.vincemann.springrapid.acl.proxy;

import com.github.vincemann.springrapid.commons.Lists;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import com.github.vincemann.springrapid.core.proxy.CrudServiceExtension;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.jpa.JPACrudService;
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
import org.springframework.data.jpa.repository.JpaRepository;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class CrudSecurityServiceProxyTest {

    @AllArgsConstructor
    @NoArgsConstructor
    class ExampleEntity extends IdentifiableEntityImpl<Long>{
        String name;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    class Entity extends IdentifiableEntityImpl<Long> {
        String name;
    }

    interface Service extends CrudService<Entity, Long, JpaRepository<Entity, Long>>{
    }

    class ServiceImpl extends JPACrudService<Entity, Long, JpaRepository<Entity, Long>>
            implements Service{

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

    class DefaultSecurityExtension extends SecurityServiceExtension<CrudService> implements CrudServiceExtension<CrudService>{


    }

    Service proxy;
    @Spy
    Service service;

    @Spy
    DefaultSecurityExtension defaultSecurityExtension;

    @Spy
    FooSecurityExtension fooSecurityExtension;

    @Mock
    Entity entity;

    @BeforeEach
    void setUp() {
        proxy = new SecurityExtensionServiceProxyFactory(defaultSecurityExtension).create(service,fooSecurityExtension);
    }

    @Test
    public void testCallDefaultRule() throws BadEntityException {
        Long id = 42L;
        InOrder inOrder = new InOrderImpl(Lists.newArrayList(service,defaultSecurityExtension,fooSecurityExtension));
        proxy.findById(id);
        inOrder.verify(fooSecurityExtension).findById(id);
        inOrder.verify(defaultSecurityExtension).findById(id);
        inOrder.verify(service).findById(id);
    }

    @Test
    public void testOverrideDefaultRule() throws BadEntityException {
        InOrder inOrder = new InOrderImpl(Lists.newArrayList(service,fooSecurityExtension));
        proxy.save(entity);
        inOrder.verify(fooSecurityExtension).save(entity);
        inOrder.verify(service).save(entity);

        Mockito.verify(defaultSecurityExtension,Mockito.never()).save(any());
    }

    @Test
    public void testOverrideDefaultRule_callNonOverridingMethod() throws BadEntityException {
        testOverrideDefaultRule();
        testCallDefaultRule();
    }
}