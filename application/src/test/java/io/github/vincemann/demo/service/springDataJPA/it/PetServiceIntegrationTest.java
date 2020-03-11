package io.github.vincemann.demo.service.springDataJPA.it;

import io.github.vincemann.demo.model.Pet;
import io.github.vincemann.demo.model.PetType;
import io.github.vincemann.demo.service.PetService;
import io.github.vincemann.demo.service.PetTypeService;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.test.service.CrudServiceIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static io.github.vincemann.generic.crud.lib.test.service.request.CrudServiceRequestBuilders.save;
import static io.github.vincemann.generic.crud.lib.test.service.result.matcher.compare.CompareEntityMatchers.compare;

@DataJpaTest
@ActiveProfiles(value = {"test","service"})
@Transactional
class PetServiceIntegrationTest
        extends CrudServiceIntegrationTest<PetService,Pet,Long> {

    @Autowired
    private PetTypeService petTypeService;
    private PetType dogPetType;



    @BeforeEach
    public void setUp() throws Exception {
        super.setup();
        this.dogPetType= petTypeService.save(new PetType("Dog"));
    }

    @Test
    public void savePetWithPersistedPetType_ShouldSucceed() throws BadEntityException {
        Pet dogWithDogType = Pet.builder()
                .petType(dogPetType)
                .birthDate(LocalDate.now())
                .name("bello")
                .build();
        getTestTemplate()
                .perform(save(dogWithDogType))
                .andExpect(compare(dogWithDogType)
                        .withDbEntity()
                        .fullEqualCheck()
                        .ignoreId()
                        .isEqual());
    }
}