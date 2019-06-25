package io.github.vincemann.demo.service.springDataJPA.it;

import io.github.vincemann.demo.model.Vet;
import io.github.vincemann.demo.service.springDataJPA.VetJPAService;
import io.github.vincemann.demo.jpaRepositories.VetRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import io.github.vincemann.generic.crud.lib.test.service.CrudServiceTest;

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
