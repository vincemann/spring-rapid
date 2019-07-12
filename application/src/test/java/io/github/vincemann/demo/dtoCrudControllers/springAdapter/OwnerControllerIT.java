package io.github.vincemann.demo.dtoCrudControllers.springAdapter;


import io.github.vincemann.demo.dtoCrudControllers.EntityInitializerControllerIT;
import io.github.vincemann.demo.dtoCrudControllers.OwnerController;
import io.github.vincemann.demo.dtos.OwnerDto;
import io.github.vincemann.demo.model.Owner;
import io.github.vincemann.demo.service.OwnerService;
import io.github.vincemann.demo.service.PetService;
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
class OwnerControllerIT extends EntityInitializerControllerIT<Owner, OwnerDto, OwnerService, OwnerController,Long> {

    @Autowired
    private PetService petService;

    OwnerControllerIT(@Autowired OwnerController crudController) {
        super(crudController, 99L);
    }

    @Override
    protected List<OwnerDto> provideValidTestDTOs() {
        return Arrays.asList(
                //OwnerDto with pets
                /*OwnerDto.builder()
                        .firstName("Hans")
                        .lastName("meier")
                        .address("MegaNiceStreet 5")
                        .city("Berlin")
                        .pets(Collections.singleton(pet))
                        .build(),*/


                //OwnerDto without pets
                OwnerDto.builder()
                        .firstName("Max")
                        .lastName("Müller")
                        .address("Andere Street 13")
                        .city("München")
                        .build(),

                //Owner with persisted pet
                OwnerDto.builder()
                        .firstName("Hans")
                        .lastName("Müller")
                        .address("mega nice Street 42")
                        .city("Berlin")
                        .petIds(Collections.singleton(getTestPet().getId()))
                        .build()

                //OwnerDto with many Pets
                /*OwnerDto.builder()
                        .firstName("Max")
                        .lastName("Müller")
                        .address("Andere Street 13")
                        .city("München")
                        .pets(new HashSet<>(Arrays.asList(pet,pet2,pet3)))
                        .build()*/

        );
    }

    @Override
    protected List<OwnerDto> provideInvalidTestDTOs() {
        return Arrays.asList(
                OwnerDto.builder()
                        .firstName("Hans")
                        .lastName("meier")
                        .address("MegaNiceStreet 5")
                        //blank city
                        .city("")
                        .build()
        );
    }


    @Override
    protected void modifyTestEntity(OwnerDto testEntityDTO) {
        testEntityDTO.setCity("MODIFIED");
    }
}
