package vincemann.github.generic.crud.lib.demo.service.springDataJPA.it;

import vincemann.github.generic.crud.lib.demo.model.Owner;
import vincemann.github.generic.crud.lib.demo.springDataJPA.OwnerRepository;
import vincemann.github.generic.crud.lib.demo.service.springDataJPA.OwnerJPAService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import vincemann.github.generic.crud.lib.test.service.CrudServiceTest;

//@DataJpaTest cant be used because i need autowired components from generic-crud-lib
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment =
        SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = {"test","springdatajpa"})
class OwnerJPAServiceTest extends CrudServiceTest<OwnerJPAService, Owner,Long> {

    @Autowired
    OwnerRepository ownerRepository;

    @Override
    protected CrudServiceTest.CrudServiceTestEntry<OwnerJPAService,Owner,Long> provideTestEntity() {
        return new CrudServiceTestEntry<>(
                new OwnerJPAService(ownerRepository), new Owner());
    }
}