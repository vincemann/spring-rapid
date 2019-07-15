package io.github.vincemann.demo.dtoCrudControllers.springAdapter;

import io.github.vincemann.demo.dtoCrudControllers.EntityInitializerControllerIT;
import io.github.vincemann.demo.dtoCrudControllers.VetController;
import io.github.vincemann.demo.dtos.VetDto;
import io.github.vincemann.demo.model.Vet;
import io.github.vincemann.demo.service.VetService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment =
        SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = {"test","springdatajpa"})
class VetControllerIT extends EntityInitializerControllerIT<Vet, VetDto, VetService, VetController> {


    VetControllerIT(@Autowired VetController crudController) {
        super(crudController);
    }

    @Override
    protected List<VetDto> provideValidTestDTOs() {
        return Arrays.asList(
                //Vet without Specialty
                VetDto.builder()
                     .firstName("master")
                     .lastName("Yoda")
                     .build(),
                //Vet with persisted specialty
                VetDto.builder()
                        .firstName("master")
                        .lastName("Yoda")
                        .specialtyIds(Collections.singleton(getTestSpecialty().getId()))
                        .build()
        );
    }

    @Override
    protected List<VetDto> provideInvalidTestDTOs() {
        return Arrays.asList(
                VetDto.builder()
                        .firstName("master")
                        //no last name
                        //.lastName("Yoda")
                        .build(),
                //Vet with invalid specialty
                VetDto.builder()
                        .firstName("master")
                        .lastName("Yoda")
                        .specialtyIds(Collections.singleton(-1L))
                        .build()
        );
    }

    @Override
    protected void modifyTestEntity(VetDto testEntityDTO) {
        testEntityDTO.setLastName("MODIFIED");
    }
}
