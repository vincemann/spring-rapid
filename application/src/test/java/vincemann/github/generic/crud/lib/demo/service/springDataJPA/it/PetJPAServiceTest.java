package vincemann.github.generic.crud.lib.demo.service.springDataJPA.it;

import vincemann.github.generic.crud.lib.demo.model.Pet;
import vincemann.github.generic.crud.lib.demo.service.springDataJPA.PetJPAService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import vincemann.github.generic.crud.lib.demo.springDataJPA.PetRepository;
import vincemann.github.generic.crud.lib.test.service.CrudServiceTest;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment =
        SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = {"test","springdatajpa"})
class PetJPAServiceTest extends CrudServiceTest<PetJPAService, Pet,Long> {

    @Autowired
    private PetRepository petRepository;

    @Override
    protected CrudServiceTestEntry<PetJPAService, Pet, Long> provideTestEntity() {
        return new CrudServiceTestEntry<>(
                new PetJPAService(petRepository), new Pet());
    }
}