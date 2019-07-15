package io.github.vincemann.demo.dtoCrudControllers.springAdapter;


import io.github.vincemann.demo.dtoCrudControllers.EntityInitializerControllerIT;
import io.github.vincemann.demo.dtoCrudControllers.OwnerController;
import io.github.vincemann.demo.dtos.OwnerDto;
import io.github.vincemann.demo.model.Owner;
import io.github.vincemann.demo.model.Pet;
import io.github.vincemann.demo.service.OwnerService;
import io.github.vincemann.demo.service.PetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment =
        SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = {"test","springdatajpa"})
class OwnerControllerIT extends EntityInitializerControllerIT<Owner, OwnerDto, OwnerService, OwnerController> {

    @Autowired
    private PetService petService;
    private Pet pet1;
    private Pet pet2;

    OwnerControllerIT(@Autowired OwnerController crudController) {
        super(crudController);
    }

    @BeforeEach
    @Override
    public void before() throws Exception {
        this.pet1 = petService.save(Pet.builder().name("pet1").petType(getTestPetType()).build());
        this.pet2 = petService.save(Pet.builder().name("pet2").petType(getTestPetType()).build());
        super.before();
    }

    @Override
    protected List<OwnerDto> provideValidTestDTOs() {
        return Arrays.asList(

                //OwnerDto without pets
                OwnerDto.builder()
                        .firstName("Max")
                        .lastName("Müller")
                        .address("other Street 13")
                        .city("munich")
                        .build(),

                //Owner with persisted pet
                OwnerDto.builder()
                        .firstName("Hans")
                        .lastName("Müller")
                        .address("mega nice Street 42")
                        .city("Berlin")
                        .petIds(Collections.singleton(getTestPet().getId()))
                        .build(),

                //OwnerDto with many Pets
                OwnerDto.builder()
                        .firstName("Max")
                        .lastName("Müller")
                        .address("Andere Street 13")
                        .city("München")
                        .petIds(new HashSet<>(Arrays.asList(pet1.getId(),pet2.getId())))
                        .build()

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
