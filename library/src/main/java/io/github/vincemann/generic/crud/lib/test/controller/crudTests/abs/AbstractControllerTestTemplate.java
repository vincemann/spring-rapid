package io.github.vincemann.generic.crud.lib.test.controller.crudTests.abs;

import io.github.vincemann.generic.crud.lib.controller.dtoMapper.DtoMappingContext;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.mediaTypeStrategy.DtoReadingException;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.InitializingTest;
import io.github.vincemann.generic.crud.lib.test.TestContextAware;
import io.github.vincemann.generic.crud.lib.test.controller.ControllerIntegrationTest;
import io.github.vincemann.generic.crud.lib.test.controller.EagerFetchControllerIntegrationTest;
import io.github.vincemann.generic.crud.lib.test.controller.crudTests.config.abs.ControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.crudTests.config.factory.abs.AbstractControllerTestConfigurationFactory;
import io.github.vincemann.generic.crud.lib.test.controller.requestEntityFactory.RequestEntityFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;

@Getter
@Setter
public abstract class AbstractControllerTestTemplate
        <E extends IdentifiableEntity<Id>, Id extends Serializable,C extends ControllerTestConfiguration<Id>>
        implements TestContextAware<ControllerIntegrationTest<E,Id>>
{
    //ctx gets auto injected when template is present in ioc container
    private ControllerIntegrationTest<E,Id> testContext;
    private RequestEntityFactory<Id,C> requestEntityFactory;


    public AbstractControllerTestTemplate(RequestEntityFactory<Id, C> requestEntityFactory) {
        this.requestEntityFactory = requestEntityFactory;
    }

    @Override
    public boolean supports(Class<? extends InitializingTest> contextClass) {
        return ControllerIntegrationTest.class.isAssignableFrom(contextClass);
    }

    //    @Override
//    public void setTestContext(ControllerIntegrationTest<E, Id> testContext) {
//        this.testContext =testContext;
//        //todo sketchy
//        //maybe put testContext programaticly into spring container and autowire it in whereever needed?
//        if(requestEntityFactory instanceof TestContextAware){
//            ((TestContextAware) requestEntityFactory).setTestContext(testContext);
//        }
//    }

    public ResponseEntity<String> sendRequest(RequestEntity<?> requestEntity){
        return getTestContext().getRestTemplate().exchange(requestEntity, String.class);
    }

    public <Dto extends IdentifiableEntity<Id>> Dto readFromBody(String body, Class<Dto> clazz) throws DtoReadingException {
        return getTestContext().getController().getMediaTypeStrategy().readDtoFromBody(body,clazz);
    }

    protected DtoMappingContext<Id> mappingContext(){
        return getTestContext().getDtoMappingContext();
    }
}
