package io.github.vincemann.springrapid.demo.service.jpa.it;

import io.github.vincemann.springrapid.demo.EnableProjectComponentScan;
import io.github.vincemann.springrapid.demo.model.Pet;
import io.github.vincemann.springrapid.demo.model.PetType;
import io.github.vincemann.springrapid.demo.service.PetService;
import io.github.vincemann.springrapid.demo.service.PetTypeService;
import io.github.vincemann.springrapid.core.service.exception.BadEntityException;
import io.github.vincemann.springrapid.coretest.service.CrudServiceIntegrationTest;
import io.github.vincemann.springrapid.entityrelationship.slicing.test.ImportRapidEntityRelConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static io.github.vincemann.springrapid.coretest.service.request.CrudServiceRequestBuilders.save;
import static io.github.vincemann.springrapid.coretest.service.result.matcher.compare.CompareEntityMatchers.compare;

@EnableProjectComponentScan
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
    public void save_withPersistedPetType_shouldSucceed() throws BadEntityException {
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