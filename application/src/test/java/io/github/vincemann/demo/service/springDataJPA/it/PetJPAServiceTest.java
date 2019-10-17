package io.github.vincemann.demo.service.springDataJPA.it;

import io.github.vincemann.demo.model.Pet;
import io.github.vincemann.demo.model.PetType;
import io.github.vincemann.demo.service.PetTypeService;
import io.github.vincemann.demo.service.springDataJPA.PetJPAService;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.successfulTestBundles.UpdatableSucceedingTestEntityBundle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import io.github.vincemann.generic.crud.lib.test.service.CrudServiceTest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment =
        SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = {"test","springdatajpa"})
class PetJPAServiceTest extends CrudServiceTest<PetJPAService, Pet,Long> {

    private PetTypeService petTypeService;
    private PetType dogPetType;

    public PetJPAServiceTest(@Autowired PetJPAService crudService, @Autowired PetTypeService petTypeService) {
        super(crudService);
        this.petTypeService=petTypeService;
    }

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        this.dogPetType= petTypeService.save(new PetType("Dog"));
        super.setUp();
    }

    @Override
    protected List<UpdatableSucceedingTestEntityBundle<Pet>> provideTestEntityBundles() {
        Pet dogWithDogType = Pet.builder()
                .petType(dogPetType)
                .birthDate(LocalDate.now())
                .name("bello")
                .build();


        return Arrays.asList(
                new UpdatableSucceedingTestEntityBundle<>(dogWithDogType)
        );
    }
}