package io.github.vincemann.demo.service.springDataJPA.it;

import io.github.vincemann.demo.model.Pet;
import io.github.vincemann.demo.model.PetType;
import io.github.vincemann.demo.repositories.PetRepository;
import io.github.vincemann.demo.service.PetTypeService;
import io.github.vincemann.demo.service.springDataJPA.PetJPAService;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import io.github.vincemann.generic.crud.lib.test.service.CrudServiceIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment =
        SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = {"test","springdatajpa"})
class PetJPAServiceIntegrationTest
        extends CrudServiceIntegrationTest<PetJPAService, PetRepository, Pet,Long> {

    @Autowired
    private PetTypeService petTypeService;
    private PetType dogPetType;



    @BeforeEach
    public void setUp() throws Exception {
        this.dogPetType= petTypeService.save(new PetType("Dog"));
    }

    @Test
    public void savePetWithPersistedPetType_ShouldSucceed() throws BadEntityException {
        Pet dogWithDogType = Pet.builder()
                .petType(dogPetType)
                .birthDate(LocalDate.now())
                .name("bello")
                .build();
        saveEntity_ShouldSucceed(dogWithDogType);
    }
}