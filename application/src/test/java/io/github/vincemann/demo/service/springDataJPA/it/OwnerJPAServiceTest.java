package io.github.vincemann.demo.service.springDataJPA.it;

import io.github.vincemann.demo.model.Owner;
import io.github.vincemann.demo.service.springDataJPA.OwnerJPAService;
import io.github.vincemann.demo.jpaRepositories.OwnerRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import io.github.vincemann.generic.crud.lib.test.service.CrudServiceTest;

//@DataJpaTest cant be used because i need autowired components from generic-crud-lib
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment =
        SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = {"test", "springdatajpa"})
class OwnerJPAServiceTest extends CrudServiceTest<OwnerJPAService, Owner, Long> {

    @Autowired
    OwnerRepository ownerRepository;

    @Override
    protected CrudServiceTest.CrudServiceTestEntry<OwnerJPAService, Owner, Long> provideTestEntity() {
        return new CrudServiceTestEntry<>(
                new OwnerJPAService(ownerRepository),
                Owner.builder()
                        .firstName("ownername")
                        .lastName("owner lastName")
                        .address("asljnflksamfslkmf")
                        .city("n1 city")
                        .telephone("12843723847324")
                        .build()
        );
    }
}