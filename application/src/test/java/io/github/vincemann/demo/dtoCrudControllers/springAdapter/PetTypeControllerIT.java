package io.github.vincemann.demo.dtoCrudControllers.springAdapter;

import io.github.vincemann.demo.dtoCrudControllers.PetTypeController;
import io.github.vincemann.demo.dtos.PetTypeDto;
import io.github.vincemann.demo.model.PetType;
import io.github.vincemann.demo.service.PetTypeService;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.UrlParamIdDtoCrudControllerSpringAdapterIT;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.plugins.CheckIfDbDeletedPlugin;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.plugins.ServiceDeepEqualPlugin;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.TestRequestEntityFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;

/*
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment =
        SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = {"test", "springdatajpa"})
public class PetTypeControllerIT extends UrlParamIdDtoCrudControllerSpringAdapterIT<PetType, PetTypeDto,PetTypeService, PetTypeController,Long> {

    public PetTypeControllerIT(@Autowired PetTypeController crudController,
                               @Autowired TestRequestEntityFactory testRequestEntityFactory,
                               @Autowired PlatformTransactionManager platformTransactionManager,
                               @Autowired CheckIfDbDeletedPlugin checkIfDbDeletedPlugin,
                               @Autowired ServiceDeepEqualPlugin serviceDeepEqualPlugin) {
        super(
                crudController,
                platformTransactionManager,
                testRequestEntityFactory,
                checkIfDbDeletedPlugin,
                serviceDeepEqualPlugin);
    }

}
*/