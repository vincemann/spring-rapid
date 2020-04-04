package io.github.vincemann.springrapid.demo.service.springDataJPA.it;

import io.github.vincemann.springrapid.demo.model.Pet;
import io.github.vincemann.springrapid.demo.model.PetType;
import io.github.vincemann.springrapid.demo.service.PetService;
import io.github.vincemann.springrapid.demo.service.PetTypeService;
import io.github.vincemann.springrapid.core.service.exception.BadEntityException;
import io.github.vincemann.springrapid.core.test.service.CrudServiceIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static io.github.vincemann.springrapid.core.test.service.request.CrudServiceRequestBuilders.save;
import static io.github.vincemann.springrapid.core.test.service.result.matcher.compare.CompareEntityMatchers.compare;

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