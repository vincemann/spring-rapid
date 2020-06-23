package com.github.vincemann.springrapid.core.proxy;

import com.github.vincemann.springrapid.commons.Lists;
import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import com.github.vincemann.springrapid.core.service.jpa.JPACrudService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.sf.cglib.proxy.MethodProxy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class CrudServicePluginProxyTest {


    @AllArgsConstructor
    @NoArgsConstructor
    class ExampleEntity extends IdentifiableEntityImpl<Long> {
        String name;
    }

    @NoArgsConstructor
    class ExampleService extends JPACrudService<ExampleEntity, Long, JpaRepository<ExampleEntity, Long>> {
        public ExampleEntity customMethod(String arg) {
            return new ExampleEntity("serviceCustomEntity");
        }

        @Override
        protected Class<ExampleEntity> provideEntityClass() {
            return ExampleEntity.class;
        }
    }

    class ExamplePlugin extends CrudServicePlugin {

        @CalledByProxy
        public void onBeforeSave(ExampleEntity exampleEntity) {

        }

        @ApplyIfRole(isNot = BAD_BOY_ROLE)
        @CalledByProxy
        public void onAfterSave(ExampleEntity exampleEntity, ExampleEntity res) {

        }

        @ApplyIfRole(isNot = BAD_BOY_ROLE, allowAnon = false)
        @CalledByProxy
        public void onAfterUpdate(ExampleEntity exampleEntity,Boolean full, ExampleEntity res) {

        }

        @CalledByProxy
        @ApplyIfRole(is = ADMIN_ROLE, isNot = SPECIFIC_ADMIN_ROLE)
        public void onAfterDeleteById(Long id){
            //admin stuff
        }
    }



    public static final String BAD_BOY_ROLE = "ROLE_BAD_BOI";
    public static final String ADMIN_ROLE = "ADMIN_BOI";
    public static final String SPECIFIC_ADMIN_ROLE = "SPECIFIC_ADMIN_BOI";
    public static final String NEUTRAL_ROLE = "NEUTRAL_BOI";

    //cant use mock auth template bc of cyclic dependency and maven test scope is not taken into consideration by intellij
    Authentication authenticationMock = Mockito.spy(Authentication.class);
    SecurityContext securityContextMock = Mockito.spy(SecurityContext.class);

    CrudServicePluginProxy proxy;

    @Mock
    MethodProxy methodProxy;
    @Mock
    ExampleService service;
    @Mock
    ExamplePlugin plugin;
    @Mock
    ExampleEntity entity;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        proxy = new CrudServicePluginProxy(service, Lists.newArrayList(plugin));
    }

    @Test
    public void callMethod_shouldCallPluginsBeforeMethod() throws Throwable {
        invokeProxy("save", entity);
//        proxy.save(entity);
        Mockito.verify(plugin).onBeforeSave(entity);
    }

    @Test
    public void callMethod_shouldCallPluginsAfterMethod_with_serviceResult_asLastArg() throws Throwable {
        ExampleEntity result = new ExampleEntity("res");
        when(service.save(entity))
                .thenReturn(result);
//        proxy.save(entity);
        invokeProxy("save", entity);
        Mockito.verify(plugin).onAfterSave(entity, result);
    }

    @Test
    public void call_roleLimitedMethod_with_blacklistedRole_shouldNotCallPluginMethod() throws Throwable {
        mockWithRoles(BAD_BOY_ROLE);
        invokeProxy("save", entity);
//        proxy.save(entity);
        Mockito.verify(plugin,Mockito.never()).onAfterSave(any(ExampleEntity.class),any(ExampleEntity.class));
    }

    @Test
    public void call_roleLimitedMethod_with_requiredRole_shouldCallPluginMethod() throws Throwable {
        mockWithRoles(ADMIN_ROLE);
        invokeProxy("deleteById", 42L);
//        proxy.deleteById(42L);
        Mockito.verify(plugin).onAfterDeleteById(any(Long.class));
    }

    @Test
    public void call_roleLimitedMethod_without_requiredRole_shouldNotCallPluginMethod() throws Throwable {
        mockWithRoles(NEUTRAL_ROLE);
        invokeProxy("deleteById", 42L);
//        proxy.deleteById(42L);
        Mockito.verify(plugin,Mockito.never()).onAfterDeleteById(any(Long.class));
    }

    @Test
    public void call_roleLimitedMethod_with_required_and_blacklisted_Role_shouldNotCallPluginMethod() throws Throwable {
        mockWithRoles(ADMIN_ROLE,SPECIFIC_ADMIN_ROLE);
//        proxy.deleteById(42L);
        invokeProxy("deleteById", 42L);
        Mockito.verify(plugin,Mockito.never()).onAfterDeleteById(any(Long.class));
    }

    @Test
    public void call_roleLimitedMethod_withDontAllowAnon_as_anon_shouldNotCallPluginMethod() throws Throwable {
        invokeProxy("update", entity,true);
//        proxy.update(entity,true);
        Mockito.verify(plugin,Mockito.never()).onAfterUpdate(any(ExampleEntity.class),any(Boolean.class),any(ExampleEntity.class));
    }

    private <T> T invokeProxy(String methodName, Object... args) throws Throwable {
        Method serviceMethod = Arrays.stream(service.getClass().getMethods())
                .filter(m -> m.getName().equals(methodName))
                .findFirst().get();
        return (T) proxy.intercept(service, serviceMethod, args,null);
    }

    private void mockWithRoles(String... roles) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("test", "test",
                Arrays.stream(roles)
                        .map(r -> new SimpleGrantedAuthority(r))
                        .collect(Collectors.toSet())
        );
        when(securityContextMock.getAuthentication()).thenReturn(token);
        SecurityContextHolder.setContext(securityContextMock);
    }

    @AfterEach
    void tearDown() {
        when(securityContextMock.getAuthentication()).thenReturn(null);
    }

    //see some analog tests in CrudServiceSecurityProxyTest for oder and entityClassArg appending



}