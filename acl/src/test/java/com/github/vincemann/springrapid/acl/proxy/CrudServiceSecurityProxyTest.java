package com.github.vincemann.springrapid.acl.proxy;

import com.github.vincemann.springrapid.commons.Lists;
import com.github.vincemann.springrapid.acl.proxy.rules.DontCallTargetMethod;
import com.github.vincemann.springrapid.acl.proxy.rules.OverrideDefaultSecurityRule;
import com.github.vincemann.springrapid.acl.proxy.rules.ServiceSecurityRule;
import com.github.vincemann.springrapid.acl.SecurityChecker;
import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import com.github.vincemann.springrapid.core.proxy.CalledByProxy;
import com.github.vincemann.springrapid.core.service.jpa.JPACrudService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.InOrderImpl;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrudServiceSecurityProxyTest {

    @AllArgsConstructor
    @NoArgsConstructor
    class ExampleEntity extends IdentifiableEntityImpl<Long>{
        String name;
    }

    class ExampleRule extends ServiceSecurityRule{

        @CalledByProxy
        public void preAuthorizeCustomMethod(String s){}

        @CalledByProxy
        public ExampleEntity postAuthorizeCustomMethod(String s,ExampleEntity result){
            return new ExampleEntity("onAfterCustomMethodEntity");
        }

        @CalledByProxy
        public void preAuthorizeSave(ExampleEntity exampleEntity){

        }

        @CalledByProxy
        public void preAuthorizeUpdate(ExampleEntity exampleEntity,boolean full, Class entityClass){

        }

        @CalledByProxy
        @OverrideDefaultSecurityRule
        public void postAuthorizeUpdate(ExampleEntity exampleEntity, boolean full, ExampleEntity ret, Class entityClass){

        }

        @CalledByProxy
        @DontCallTargetMethod
        public void preAuthorizeDeleteById(Long id){

        }

        @CalledByProxy
        @OverrideDefaultSecurityRule
        public void preAuthorizeFindById(Long id){

        }

        @CalledByProxy
        public void postAuthorizeSave(ExampleEntity exampleEntity,ExampleEntity returned){

        }
    }

    class DefaultRule extends ServiceSecurityRule{

        @CalledByProxy
        public void preAuthorizeSave(ExampleEntity exampleEntity){

        }

        @CalledByProxy
        public void preAuthorizeCustomMethod(String s){}

        @CalledByProxy
        public void postAuthorizeSave(ExampleEntity exampleEntity,ExampleEntity returned){

        }

        @CalledByProxy
        public void preAuthorizeDeleteById(Long id){

        }

        @CalledByProxy
        public void preAuthorizeFindById(Long id){
            //should never be called
        }

        @CalledByProxy
        public void postAuthorizeFindById(Long id, Optional<ExampleEntity> result){
            //should be called
        }

        @CalledByProxy
        public void postAuthorizeUpdate(ExampleEntity exampleEntity, boolean full, ExampleEntity ret, Class entityClass){

        }

    }

    class ExampleService extends JPACrudService<ExampleEntity,Long, JpaRepository<ExampleEntity,Long>> {
        public ExampleEntity customMethod(String arg){
            return new ExampleEntity("serviceCustomEntity");
        }

        @Override
        public Class<?> getTargetClass() {
            return ExampleService.class;
        }
    }

    CrudServiceSecurityProxy proxy;
    @Mock
    ExampleService service;
    @Mock
    SecurityChecker securityChecker;
    @Mock
    ExampleRule rule;
    @Mock
    DefaultRule defaultRule;
    @Mock
    ExampleEntity exampleEntity;

    @BeforeEach
    void setUp() {
        proxy = new CrudServiceSecurityProxy(service,securityChecker,defaultRule,rule);
    }

    @Test
    public void callMethod_shouldCall_rulesBeforeMethod() throws Throwable {
        invokeProxy("save",exampleEntity);

        Mockito.verify(rule).preAuthorizeSave(exampleEntity);
        Mockito.verify(rule,Mockito.never()).preAuthorizeCustomMethod(anyString());
    }
    @Test
    public void callMethod_withBeforeRule_shouldBeCalled_with_entityClass_asLastArg() throws Throwable {
        invokeProxy("update",exampleEntity,true);

        Mockito.verify(rule).preAuthorizeUpdate(exampleEntity,true,service.getEntityClass());
    }

    @Test
    public void callMethod_withAfterRule_shouldBeCalled_with_entityClass_asLastArg() throws Throwable {
        invokeProxy("update",exampleEntity,true);

        Mockito.verify(rule).postAuthorizeUpdate(exampleEntity,true,null,service.getEntityClass());
    }

    @Test
    public void callMethod_shouldCall_serviceMethod() throws Throwable {
        invokeProxy("save",exampleEntity);

        Mockito.verify(service).save(exampleEntity);
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    public void callMethod_shouldCall_defaultRulesBeforeMethod() throws Throwable {
        invokeProxy("save",exampleEntity);

        Mockito.verify(defaultRule).preAuthorizeSave(exampleEntity);
    }

    @Test
    public void callMethod_shouldCall_afterMethodInRule_withServiceArgs_and_returnedValue() throws Throwable {
        ExampleEntity returned = new ExampleEntity("returned");
        when(service.save(exampleEntity))
                .thenReturn(returned);
        invokeProxy("save",exampleEntity);

        Mockito.verify(rule).postAuthorizeSave(exampleEntity,returned);
    }

    @Test
    public void callMethod_shouldCall_defaultRulesAfterMethod() throws Throwable {
        ExampleEntity returned = new ExampleEntity("returned");
        when(service.save(exampleEntity))
                .thenReturn(returned);
        invokeProxy("save",exampleEntity);

        Mockito.verify(defaultRule).postAuthorizeSave(exampleEntity,returned);
    }

    @Test
    public void callCustomMethod_shouldCallRulesBeforeMethods_and_serviceMethod() throws Throwable {
        String arg = "argString";
        invokeProxy("customMethod",arg);

        Mockito.verify(defaultRule).preAuthorizeCustomMethod(arg);
        Mockito.verify(rule).preAuthorizeCustomMethod(arg);
        Mockito.verify(service).customMethod(arg);
    }



    @Test
    public void callMethod_withDontCallTargetMethodConfig_targetMethod_shouldNotBeCalled() throws Throwable {
        Long id =42L;
        invokeProxy("deleteById",id);

        Mockito.verify(rule).preAuthorizeDeleteById(id);
        Mockito.verify(defaultRule).preAuthorizeDeleteById(id);
        Mockito.verifyNoInteractions(service);

    }

    @Test
    public void callMethod_withOverrideDefaultRuleConfig_defaultPreAuthMethod_shouldNotBeCalled() throws Throwable {
        Long id =42L;
        invokeProxy("findById",id);

        Mockito.verify(rule).preAuthorizeFindById(id);
        Mockito.verify(defaultRule,never()).preAuthorizeFindById(id);
        Mockito.verify(service).findById(id);
    }

    @Test
    public void callMethod_withOverrideDefaultRuleConfig_defaultPostAuthMethod_shouldNotBeCalled() throws Throwable {
        invokeProxy("update",exampleEntity,true);

        Mockito.verify(rule).postAuthorizeUpdate(exampleEntity,true,null,service.getEntityClass());
        Mockito.verify(defaultRule,never()).postAuthorizeUpdate(any(ExampleEntity.class),anyBoolean(),any(ExampleEntity.class),any(Class.class));
    }

    @Test
    public void callMethod_withOverrideDefaultRuleConfig_defaultPreAuthMethod_shouldNotBeCalled_but_DefaultRulePostMethod_should() throws Throwable {
        Long id =42L;
        Optional<ExampleEntity> serviceResult = Optional.of(exampleEntity);
        when(service.findById(id))
                .thenReturn(serviceResult);
        invokeProxy("findById",id);

        Mockito.verify(defaultRule).postAuthorizeFindById(id,serviceResult);
    }

    @Test
    public void callMethod_orderOfPlugins_shouldMatch_orderOfExecution() throws Throwable {
        ExampleRule newRule = Mockito.mock(ExampleRule.class);
        proxy.getRules().add(newRule);

        invokeProxy("save",exampleEntity);

        InOrder inOrder = new InOrderImpl(Lists.newArrayList(newRule,rule));
        inOrder.verify(rule).postAuthorizeSave(exampleEntity,null);
        inOrder.verify(newRule).postAuthorizeSave(exampleEntity,null);
    }

    @Test
    public void callMethod_testOrder_pre_preDef_service_post_postDef_Method() throws Throwable {
        invokeProxy("save",exampleEntity);

        InOrder inOrder = new InOrderImpl(Lists.newArrayList(rule,service,defaultRule));
        inOrder.verify(rule).preAuthorizeSave(exampleEntity);
        inOrder.verify(defaultRule).preAuthorizeSave(exampleEntity);
        inOrder.verify(service).save(exampleEntity);
        inOrder.verify(rule).postAuthorizeSave(exampleEntity,null);
        inOrder.verify(defaultRule).postAuthorizeSave(exampleEntity,null);
    }

    private <T> T invokeProxy(String methodName, Object... args) throws Throwable {
        Method customMethod
                = Arrays.stream(service.getClass().getMethods()).filter(m -> m.getName().equals(methodName)).findFirst().get();
        return (T) proxy.invoke(service,customMethod, args);
    }
}