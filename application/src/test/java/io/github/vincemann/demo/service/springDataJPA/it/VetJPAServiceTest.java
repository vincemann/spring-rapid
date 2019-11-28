package io.github.vincemann.demo.service.springDataJPA.it;

import io.github.vincemann.demo.model.Vet;
import io.github.vincemann.demo.service.springDataJPA.VetJPAService;
import io.github.vincemann.generic.crud.lib.test.testBundles.service.save.SuccessfulSaveServiceTestBundle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import io.github.vincemann.generic.crud.lib.test.service.CrudServiceTest;

import java.util.Arrays;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment =
        SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = {"test", "springdatajpa"})
public class VetJPAServiceTest extends CrudServiceTest<VetJPAService, Vet, Long> {

    public VetJPAServiceTest(@Autowired VetJPAService crudService) {
        super(crudService, transactionManager, repository);
    }


    @Override
    protected List<SuccessfulSaveServiceTestBundle<Vet>> provideSuccessfulSaveTestEntityBundles() {
        return Arrays.asList(
                new SuccessfulSaveServiceTestBundle<>(
                        Vet.builder()
                        .firstName("meister")
                        .lastName("yoda")
                        .build())
        );
    }

}
