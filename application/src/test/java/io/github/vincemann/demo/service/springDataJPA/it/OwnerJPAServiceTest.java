package io.github.vincemann.demo.service.springDataJPA.it;

import io.github.vincemann.demo.model.Owner;
import io.github.vincemann.demo.service.springDataJPA.OwnerJPAService;
import io.github.vincemann.generic.crud.lib.test.testBundles.service.save.SuccessfulSaveServiceTestBundle;
import io.github.vincemann.generic.crud.lib.test.testBundles.service.update.SuccessfulUpdateServiceTestEntityBundle;
import org.junit.jupiter.api.BeforeEach;
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

    private Owner validOwner;

    public OwnerJPAServiceTest(@Autowired OwnerJPAService crudService) {
        super(crudService, transactionManager, repository);
    }


    @Override
    @BeforeEach
    public void setUp() throws Exception {
        validOwner  = Owner.builder()
                .firstName("ownername")
                .lastName("owner lastName")
                .address("asljnflksamfslkmf")
                .city("n1 city")
                .telephone("12843723847324")
                .build();
        super.setUp();
    }

    @Override
    protected List<SuccessfulSaveServiceTestBundle<Owner>> provideSuccessfulSaveTestEntityBundles() {
        return Arrays.asList(
                new SuccessfulSaveServiceTestBundle<>(validOwner)
        );
    }

    @Override
    protected List<SuccessfulUpdateServiceTestEntityBundle<Owner>> provideSuccessfulUpdateTestEntityBundles() {
        Owner diffTelephoneNumberUpdate = Owner.builder()
                .firstName("ownername")
                .lastName("owner lastName")
                .address("asljnflksamfslkmf")
                .city("n1 city")
                .telephone("42")
                .build();


        return Arrays.asList(
            new SuccessfulUpdateServiceTestEntityBundle<>(validOwner,diffTelephoneNumberUpdate)
        );
    }

}