package io.github.vincemann.generic.crud.lib.test.controller.crudTests;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.controller.ControllerIntegrationTest;
import io.github.vincemann.generic.crud.lib.test.controller.crudTests.abs.AbstractControllerTestTemplate;
import io.github.vincemann.generic.crud.lib.test.controller.crudTests.config.abs.ControllerTestConfiguration;
import io.github.vincemann.generic.crud.lib.test.controller.crudTests.config.factory.abs.AbstractControllerTestConfigurationFactory;
import io.github.vincemann.generic.crud.lib.test.controller.requestEntityFactory.FindAllTest;
import io.github.vincemann.generic.crud.lib.test.controller.requestEntityFactory.RequestEntityFactory;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@TestComponent
public class FindAllControllerTestTemplate<E extends IdentifiableEntity<Id>, Id extends Serializable>
        extends AbstractControllerTestTemplate<E,Id,ControllerTestConfiguration<Id>>
{


    private AbstractControllerTestConfigurationFactory<E, Id, ControllerTestConfiguration<Id>, ControllerTestConfiguration<Id>> configFactory;

    public FindAllControllerTestTemplate(@FindAllTest RequestEntityFactory<Id, ControllerTestConfiguration<Id>> requestEntityFactory, @FindAllTest AbstractControllerTestConfigurationFactory<E, Id, ControllerTestConfiguration<Id>, ControllerTestConfiguration<Id>> configFactory) {
        super(requestEntityFactory);
        this.configFactory = configFactory;
    }

    public <Dto extends IdentifiableEntity<Id>> Set<Dto> findAllEntities_ShouldSucceed() throws Exception {
        return findAllEntities_ShouldSucceed(configFactory.createDefaultSuccessfulConfig());
    }


    public <Dto extends IdentifiableEntity<Id>> Set<Dto> findAllEntities_ShouldSucceed(ControllerTestConfiguration<Id>... modifications) throws Exception {
        ControllerTestConfiguration<Id> config = configFactory.createMergedSuccessfulConfig(modifications);
        ResponseEntity<String> responseEntity = findAllEntities(config);

        @SuppressWarnings("unchecked")
        Set<IdentifiableEntity<Id>> httpResponseDtos = getTestContext().getController().getMediaTypeStrategy().readDtosFromBody(responseEntity.getBody(), mappingContext().getFindAllReturnDtoClass(), Set.class);
        //check if dto type is correct
        List<Id> idsSeen = new ArrayList<>();
        for (IdentifiableEntity<Id> dto : httpResponseDtos) {
            Assertions.assertEquals(mappingContext().getFindAllReturnDtoClass(),dto.getClass());
            //prevent duplicates
            Assertions.assertFalse(idsSeen.contains(dto.getId()));
            idsSeen.add(dto.getId());
        }
        return (Set<Dto>) httpResponseDtos;
    }



    public ResponseEntity<String> findAllEntities_ShouldFail() throws Exception {
        return findAllEntities_ShouldFail(configFactory.createDefaultFailedConfig());
    }


    public ResponseEntity<String> findAllEntities_ShouldFail(ControllerTestConfiguration<Id>... modifications) throws Exception {
        ControllerTestConfiguration<Id> config = configFactory.createMergedFailedConfig(modifications);
        return findAllEntities(config);
    }

    public ResponseEntity<String> findAllEntities(ControllerTestConfiguration<Id> config) {
        return sendRequest(getRequestEntityFactory().create(config,null,null));
    }
}
