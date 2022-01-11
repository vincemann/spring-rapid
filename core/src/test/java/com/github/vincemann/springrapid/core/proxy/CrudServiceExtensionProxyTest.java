package com.github.vincemann.springrapid.core.proxy;

import com.github.vincemann.springrapid.core.service.RapidJpaRepository;
import com.github.vincemann.springrapid.core.util.Lists;
import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.JPACrudService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.internal.InOrderImpl;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.TargetClassAware;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import static org.mockito.ArgumentMatchers.any;


@ExtendWith(MockitoExtension.class)
class CrudServiceExtensionProxyTest {


    @AllArgsConstructor
    @NoArgsConstructor
    class Entity extends IdentifiableEntityImpl<Long> {
        String name;
    }

    interface Service extends CrudService<Entity, Long>, SubInterface, OverlappingInterface{

        public void noExtensionKnowsMe();
    }

    class ServiceImpl extends JPACrudService<Entity, Long, RapidJpaRepository<Entity, Long>>
            implements Service, TargetClassAware {

        @Override
        public Class<?> getTargetClass() {
            return ServiceImpl.class;
        }

        @Override
        public Class overlappingMethod() {
            return ServiceImpl.class;
        }

        @Override
        public void onceOnlyMethod() {

        }

        @Override
        public void noExtensionKnowsMe() {

        }
    }

    interface OverlappingInterface extends SubInterface {
        public void onceOnlyMethod();
    }

    interface SubInterface {
        public Class overlappingMethod();
    }

    class FooCrudServiceExtension
            extends BasicServiceExtension<CrudService>
                    implements CrudServiceExtension<CrudService>{
    }

    class OverlappingExtension extends BasicServiceExtension<OverlappingInterface> implements OverlappingInterface{

        @Override
        public void onceOnlyMethod() {
            getNext().onceOnlyMethod();
        }

        @Override
        public Class overlappingMethod() {
            getNext().overlappingMethod();
            return OverlappingExtension.class;
        }
    }

    class SubExtension extends BasicServiceExtension<SubInterface> implements SubInterface{
        @Override
        public Class overlappingMethod() {
            return getNext().overlappingMethod();
        }
    }


    Service proxy;
    @Spy
    Service service;
    
    @Spy
    SubExtension subExtension;
    @Spy
    OverlappingExtension overlappingExtension;
    @Spy
    FooCrudServiceExtension serviceExtension;
    @Mock
    Entity entity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        proxy = new ServiceExtensionProxyBuilder<>(service)
                .addExtensions(subExtension,serviceExtension,overlappingExtension)
                .build();
    }

    @Test
    public void invokeOverlappingMethod() throws Throwable {
        InOrder inOrder = new InOrderImpl(Lists.newArrayList(service,subExtension,overlappingExtension));
        proxy.overlappingMethod();
        inOrder.verify(subExtension).overlappingMethod();
        inOrder.verify(overlappingExtension).overlappingMethod();
        inOrder.verify(service).overlappingMethod();
    }

    @Test
    public void invokeOnlyOnceMethod() throws Throwable {
        InOrder inOrder = new InOrderImpl(Lists.newArrayList(service,overlappingExtension));
        proxy.onceOnlyMethod();
        inOrder.verify(overlappingExtension).onceOnlyMethod();
        inOrder.verify(service).onceOnlyMethod();
    }

    @Test
    public void invokeServiceExtensionOnlyMethod() throws Throwable {
        InOrder inOrder = new InOrderImpl(Lists.newArrayList(service,serviceExtension));
        proxy.save(entity);
        inOrder.verify(serviceExtension).save(entity);
        inOrder.verify(service).save(entity);
    }


    @Test
    public void invokeServiceOnlyMethod() throws Throwable {
        proxy.noExtensionKnowsMe();
        Mockito.verify(service).noExtensionKnowsMe();
    }

    @Test
    public void extensionModifiesReturnValue() throws Throwable {
        Class result = proxy.overlappingMethod();
        Assertions.assertEquals(OverlappingExtension.class,result);
    }

//    @Test
//    public void callMethod_shouldCallExtensionsAfterMethod_with_serviceResult_asLastArg() throws Throwable {
//        Entity result = new Entity("res");
//        Mockito.when(service.save(entity))
//                .thenReturn(result);
//        invokeProxy("save", entity);
//        Mockito.verify(fooCrudServiceExtension).onAfterSave(entity, result);
//    }
//
//    @Test
//    public void call_roleLimitedMethod_with_blacklistedRole_shouldNotCallExtensionMethod() throws Throwable {
//        mockWithRoles(BAD_BOY_ROLE);
//        invokeProxy("save", entity);
//        Mockito.verify(fooCrudServiceExtension,Mockito.never()).onAfterSave(any(Entity.class),any(Entity.class));
//    }
//
//    @Test
//    public void call_roleLimitedMethod_with_requiredRole_shouldCallExtensionMethod() throws Throwable {
//        mockWithRoles(ADMIN_ROLE);
//        invokeProxy("deleteById", 42L);
//        Mockito.verify(fooCrudServiceExtension).onAfterDeleteById(any(Long.class));
//    }
//
//    @Test
//    public void call_roleLimitedMethod_without_requiredRole_shouldNotCallExtensionMethod() throws Throwable {
//        mockWithRoles(NEUTRAL_ROLE);
//        invokeProxy("deleteById", 42L);
//        Mockito.verify(fooCrudServiceExtension,Mockito.never()).onAfterDeleteById(any(Long.class));
//    }
//
//    @Test
//    public void call_roleLimitedMethod_with_required_and_blacklisted_Role_shouldNotCallExtensionMethod() throws Throwable {
//        mockWithRoles(ADMIN_ROLE,SPECIFIC_ADMIN_ROLE);
//        invokeProxy("deleteById", 42L);
//        Mockito.verify(fooCrudServiceExtension,Mockito.never()).onAfterDeleteById(any(Long.class));
//    }
//
//    @Test
//    public void call_roleLimitedMethod_withDontAllowAnon_as_anon_shouldNotCallExtensionMethod() throws Throwable {
//        invokeProxy("update", entity,true);
//        Mockito.verify(fooCrudServiceExtension,Mockito.never()).onAfterUpdate(any(Entity.class),any(Boolean.class),any(Entity.class));
//    }
//
//    private <T> T invokeProxy(String methodName, Object... args) throws Throwable {
//        Method customMethod = Arrays.stream(service.getClass().getMethods())
//                .filter(m -> m.getName().equals(methodName))
//                .findFirst().get();
//        return (T) proxy.invoke(service, customMethod, args);
//    }
//
//    private void mockWithRoles(String... roles) {
//        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("test", "test",
//                Arrays.stream(roles)
//                        .map(r -> new SimpleGrantedAuthority(r))
//                        .collect(Collectors.toSet())
//        );
//        Mockito.when(securityContextMock.getAuthentication()).thenReturn(token);
//        SecurityContextHolder.setContext(securityContextMock);
//    }
//
//    @AfterEach
//    void tearDown() {
//        Mockito.when(securityContextMock.getAuthentication()).thenReturn(null);
//    }
//
//    //see some analog tests in CrudServiceSecurityProxyTest for oder and entityClassArg appending



}