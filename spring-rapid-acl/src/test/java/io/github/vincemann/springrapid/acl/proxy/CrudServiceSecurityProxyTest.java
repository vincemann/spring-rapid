package io.github.vincemann.springrapid.acl.proxy;

import io.github.vincemann.springrapid.acl.proxy.rules.ServiceSecurityRule;
import io.github.vincemann.springrapid.acl.securityChecker.SecurityChecker;
import io.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import io.github.vincemann.springrapid.core.proxy.CalledByProxy;
import io.github.vincemann.springrapid.core.service.jpa.JPACrudService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

    }

    class ExampleService extends JPACrudService<ExampleEntity,Long, JpaRepository<ExampleEntity,Long>> {
        public ExampleEntity customMethod(String arg){
            return new ExampleEntity("serviceCustomEntity");
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
    public void callSaveMethod_shouldCallBeforeMethodInRule() throws Throwable {
        invokeProxy("save",exampleEntity);

        Mockito.verify(rule).preAuthorizeSave(exampleEntity);
        Mockito.verify(rule,Mockito.never()).preAuthorizeCustomMethod(anyString());
    }

    @Test
    public void callSaveMethod_shouldCallServiceMethod() throws Throwable {
        invokeProxy("save",exampleEntity);

        Mockito.verify(service).save(exampleEntity);
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    public void callSaveMethod_shouldCallDefaultRuleBeforeMethod() throws Throwable {
        invokeProxy("save",exampleEntity);

        Mockito.verify(defaultRule).preAuthorizeSave(exampleEntity);
    }





    @Test
    public void callSaveMethod_shouldCallAfterMethodInRule_withServiceArgsAndReturnedValue() throws Throwable {
        ExampleEntity returned = new ExampleEntity("returned");
        when(service.save(exampleEntity))
                .thenReturn(returned);
        invokeProxy("save",exampleEntity);

        Mockito.verify(rule).postAuthorizeSave(exampleEntity,returned);
    }

    @Test
    public void callSaveMethod_shouldCallDefaultRuleAfterMethod() throws Throwable {
        ExampleEntity returned = new ExampleEntity("returned");
        when(service.save(exampleEntity))
                .thenReturn(returned);
        invokeProxy("save",exampleEntity);

        Mockito.verify(defaultRule).postAuthorizeSave(exampleEntity,returned);
    }



    @Test
    public void callCustomMethod_shouldCallRulesBeforeMethodsAndServiceMethod() throws Throwable {
        String arg = "argString";
        invokeProxy("customMethod",arg);

        Mockito.verify(defaultRule).preAuthorizeCustomMethod(arg);
        Mockito.verify(rule).preAuthorizeCustomMethod(arg);
        Mockito.verify(service).customMethod(arg);
    }




    private <T> T invokeProxy(String methodName, Object... args) throws Throwable {
        Method customMethod
                = Arrays.stream(service.getClass().getMethods()).filter(m -> m.getName().equals(methodName)).findFirst().get();
        return (T) proxy.invoke(service,customMethod, args);
    }
}