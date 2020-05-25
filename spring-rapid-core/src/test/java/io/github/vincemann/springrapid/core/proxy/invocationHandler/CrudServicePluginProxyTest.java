package io.github.vincemann.springrapid.core.proxy.invocationHandler;

import io.github.vincemann.springrapid.commons.Lists;
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
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.Method;
import java.util.Arrays;


@ExtendWith(MockitoExtension.class)
class CrudServicePluginProxyTest {

    @AllArgsConstructor
    @NoArgsConstructor
    class ExampleEntity extends IdentifiableEntityImpl<Long> {
        String name;
    }

    class ExampleService extends JPACrudService<ExampleEntity,Long, JpaRepository<ExampleEntity,Long>> {
        public ExampleEntity customMethod(String arg){
            return new ExampleEntity("serviceCustomEntity");
        }
    }

    class ExamplePlugin extends CrudServicePlugin{

        @CalledByProxy
        public void onBeforeSave(ExampleEntity exampleEntity){

        }

        @CalledByProxy
        public void onAfterSave(ExampleEntity exampleEntity,ExampleEntity res){

        }
    }

    CrudServicePluginProxy proxy;
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
    public void callMethod_shouldCall_pluginsBeforeMethod() throws Throwable {
        invokeProxy("save",entity);
        Mockito.verify(plugin).onBeforeSave(entity);
    }

    @Test
    public void callMethod_shouldCall_pluginsAfterMethod_with_serviceResult_asLastArg() throws Throwable {
        ExampleEntity result = new ExampleEntity("res");
        Mockito.when(service.save(entity))
                .thenReturn(result);
        invokeProxy("save",entity);
        Mockito.verify(plugin).onAfterSave(entity,result);
    }

    //see some analog tests in CrudServiceSecurityProxyTest for oder and entityClassArg appending



    private <T> T invokeProxy(String methodName, Object... args) throws Throwable {
        Method customMethod
                = Arrays.stream(service.getClass().getMethods()).filter(m -> m.getName().equals(methodName)).findFirst().get();
        return (T) proxy.invoke(service,customMethod, args);
    }


}