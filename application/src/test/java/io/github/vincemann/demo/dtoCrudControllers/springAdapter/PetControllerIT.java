package io.github.vincemann.demo.dtoCrudControllers.springAdapter;


import io.github.vincemann.demo.dtoCrudControllers.EntityInitializerControllerIT;
import io.github.vincemann.demo.dtoCrudControllers.PetController;
import io.github.vincemann.demo.dtos.PetDto;
import io.github.vincemann.demo.model.Pet;
import io.github.vincemann.demo.service.PetService;
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
@ActiveProfiles(value = {"test","springdatajpa"})
public class PetControllerIT extends EntityInitializerControllerIT<Pet, PetDto, PetService, PetController> {


    public PetControllerIT(@Autowired PetController crudController) {
        super(crudController);
    }


    @Override
    protected List<PetDto> provideValidTestDTOs() {
        return Arrays.asList(
                //Pet with persisted PetType
                PetDto.builder()
                        .name("esta")
                        .petTypeId(getTestPetType().getId())
                        .build(),
                //Pet with persisted PetType and persisted Owner
                PetDto.builder()
                        .ownerId(getTestOwner().getId())
                        .petTypeId(getTestPetType().getId())
                        .name("esta")
                        .build()
        );
    }

    @Override
    protected List<PetDto> provideInvalidTestDTOs() {
        return Arrays.asList(
                PetDto.builder()
                        .name(null)
                        .petTypeId(getTestPetType().getId())
                        .build()
        );
    }

    @Override
    protected void modifyTestEntity(PetDto testEntityDTO) {
        testEntityDTO.setName("MODIFIED NAME");
    }
}
