package vincemann.github.generic.crud.lib.demo.dtoCrudControllers.springAdapter;


import vincemann.github.generic.crud.lib.demo.dtoCrudControllers.EntityInitializerControllerIT;
import vincemann.github.generic.crud.lib.demo.dtoCrudControllers.PetController;
import vincemann.github.generic.crud.lib.demo.dtos.PetDTO;
import vincemann.github.generic.crud.lib.demo.model.Pet;
import vincemann.github.generic.crud.lib.demo.service.PetService;
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
public class PetControllerIT extends EntityInitializerControllerIT<Pet, PetDTO, PetService, PetController,Long> {


    public PetControllerIT(@Autowired PetController crudController) {
        super(crudController, 99L);
    }


    @Override
    protected List<PetDTO> provideValidTestDTOs() {
        return Arrays.asList(
                PetDTO.builder()
                        .name("esta")
                        .petType(getTestPetType())
                        .build(),
                //Pet with known Owner
                PetDTO.builder()
                        .ownerId(getTestOwner().getId())
                        .petType(getTestPetType())
                        .name("esta")
                        .build()
        );
    }

    @Override
    protected List<PetDTO> provideInvalidTestDTOs() {
        return Arrays.asList(
                PetDTO.builder()
                        .name(null)
                        .petType(getTestPetType())
                        .build()
        );
    }

    @Override
    protected void modifyTestEntity(PetDTO testEntityDTO) {
        testEntityDTO.setName("MODIFIED NAME");
    }
}
