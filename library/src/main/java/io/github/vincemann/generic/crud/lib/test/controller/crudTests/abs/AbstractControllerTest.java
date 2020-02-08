package io.github.vincemann.generic.crud.lib.test.controller.crudTests.abs;

import io.github.vincemann.generic.crud.lib.controller.dtoMapper.DtoMappingContext;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.mediaTypeStrategy.DtoReadingException;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.controller.ControllerIntegrationTest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public abstract class AbstractControllerTest<E extends IdentifiableEntity<Id>, Id extends Serializable> {
    private ControllerIntegrationTest<E,Id> testContext;



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
