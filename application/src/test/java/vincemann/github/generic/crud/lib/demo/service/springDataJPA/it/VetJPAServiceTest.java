package vincemann.github.generic.crud.lib.demo.service.springDataJPA.it;

import vincemann.github.generic.crud.lib.demo.model.Vet;
import vincemann.github.generic.crud.lib.demo.service.springDataJPA.VetJPAService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import vincemann.github.generic.crud.lib.demo.springDataJPA.VetRepository;
import vincemann.github.generic.crud.lib.test.service.CrudServiceTest;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment =
        SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = {"test","springdatajpa"})
public class VetJPAServiceTest extends CrudServiceTest<VetJPAService, Vet,Long> {

    @Autowired
    VetRepository vetRepository;

    @Override
    protected CrudServiceTest.CrudServiceTestEntry<VetJPAService, Vet, Long> provideTestEntity() {
        return new CrudServiceTestEntry<>(
                new VetJPAService(vetRepository), new Vet());
    }
}
