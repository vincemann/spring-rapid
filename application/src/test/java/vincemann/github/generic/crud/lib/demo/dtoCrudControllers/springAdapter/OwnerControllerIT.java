package vincemann.github.generic.crud.lib.demo.dtoCrudControllers.springAdapter;


import vincemann.github.generic.crud.lib.demo.dtoCrudControllers.EntityInitializerControllerIT;
import vincemann.github.generic.crud.lib.demo.dtoCrudControllers.OwnerController;
import vincemann.github.generic.crud.lib.demo.dtos.OwnerDTO;
import vincemann.github.generic.crud.lib.demo.dtos.PetDTO;
import vincemann.github.generic.crud.lib.demo.model.Owner;
import vincemann.github.generic.crud.lib.demo.service.OwnerService;
import vincemann.github.generic.crud.lib.demo.service.PetService;
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
class OwnerControllerIT extends EntityInitializerControllerIT<Owner, OwnerDTO, OwnerService, OwnerController,Long> {

    @Autowired
    private PetService petService;


    OwnerControllerIT(@Autowired OwnerController crudController) {
        super(crudController, 99L);
    }


    @Override
    protected List<OwnerDTO> provideValidTestDTOs() {
        PetDTO pet = PetDTO.builder()
                .name("wau wau")
                .petType(getTestPetType())
                .build();

        PetDTO pet2 = PetDTO.builder()
                .name("Bello")
                .petType(getTestPetType())
                .build();
        PetDTO pet3 = PetDTO.builder()
                .name("Hundi")
                .petType(getTestPetType())
                .build();




        return Arrays.asList(
                //OwnerDTO with pets
                OwnerDTO.builder()
                        .firstName("Hans")
                        .lastName("meier")
                        .address("MegaNiceStreet 5")
                        .city("Berlin")
                        .pets(Collections.singleton(pet))
                        .build(),
                //OwnerDTO without pets

                OwnerDTO.builder()
                        .firstName("Max")
                        .lastName("Müller")
                        .address("Andere Street 13")
                        .city("München")
                        .pets(null)
                        .build(),
                //OwnerDTO with many Pets
                OwnerDTO.builder()
                        .firstName("Max")
                        .lastName("Müller")
                        .address("Andere Street 13")
                        .city("München")
                        .pets(new HashSet<>(Arrays.asList(pet,pet2,pet3)))
                        .build()
                //todo OwnerDTO with already persisted pet

        );
    }

    @Override
    protected List<OwnerDTO> provideInvalidTestDTOs() {
        return Arrays.asList(
                OwnerDTO.builder()
                        .firstName("Hans")
                        .lastName("meier")
                        .address("MegaNiceStreet 5")
                        //blank city
                        .city("")
                        .build()
        );
    }


    @Override
    protected void modifyTestEntity(OwnerDTO testEntityDTO) {
        testEntityDTO.setCity("MODIFIED");
    }
}
