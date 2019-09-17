package io.github.vincemann.demo.dtoCrudControllers.springAdapter;

import io.github.vincemann.demo.dtoCrudControllers.PetTypeController;
import io.github.vincemann.demo.dtos.PetTypeDto;
import io.github.vincemann.demo.model.PetType;
import io.github.vincemann.demo.service.PetTypeService;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.ValidationUrlParamIdDtoCrudControllerSpringAdapterIT;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.TestEntityBundle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment =
        SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = {"test", "springdatajpa"})
public class PetTypeControllerIT extends ValidationUrlParamIdDtoCrudControllerSpringAdapterIT<PetType, PetTypeDto,PetTypeService, PetTypeController,Long> {

    public PetTypeControllerIT(@Autowired PetTypeController crudController) {
        super(crudController, -1L);
    }

    @Override
    protected List<PetTypeDto> provideInvalidTestDtos() {
        return Arrays.asList(
                new PetTypeDto(null),
                new PetTypeDto("")
        );
    }

    @Override
    protected List<TestEntityBundle<PetTypeDto>> provideInvalidUpdateDtoBundles() {
        return null;
    }

    @Override
    protected List<TestEntityBundle<PetTypeDto>> provideValidTestDtos() {
        return Arrays.asList(
                new TestEntityBundle<>(new PetTypeDto("Maus"))
        );
    }
}
