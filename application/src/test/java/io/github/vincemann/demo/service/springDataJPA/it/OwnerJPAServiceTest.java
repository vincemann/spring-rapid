package io.github.vincemann.demo.service.springDataJPA.it;

import io.github.vincemann.demo.model.Owner;
import io.github.vincemann.demo.service.springDataJPA.OwnerJPAService;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.successfulTestBundles.UpdatableSucceedingTestEntityBundle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import io.github.vincemann.generic.crud.lib.test.service.CrudServiceTest;

import java.util.Arrays;
import java.util.List;

//@DataJpaTest cant be used because i need autowired components from generic-crud-lib
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment =
        SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = {"test", "springdatajpa"})
class OwnerJPAServiceTest extends CrudServiceTest<OwnerJPAService, Owner, Long> {


    public OwnerJPAServiceTest(@Autowired OwnerJPAService crudService) {
        super(crudService);
    }

    @Override
    protected List<UpdatableSucceedingTestEntityBundle<Owner>> provideTestEntityBundles() {
        Owner owner = Owner.builder()
                .firstName("ownername")
                .lastName("owner lastName")
                .address("asljnflksamfslkmf")
                .city("n1 city")
                .telephone("12843723847324")
                .build();

        Owner diffTelephoneNumberUpdate = Owner.builder()
                .firstName("ownername")
                .lastName("owner lastName")
                .address("asljnflksamfslkmf")
                .city("n1 city")
                .telephone("42")
                .build();

        return Arrays.asList(
                new UpdatableSucceedingTestEntityBundle<>(owner,diffTelephoneNumberUpdate)
        );
    }
}