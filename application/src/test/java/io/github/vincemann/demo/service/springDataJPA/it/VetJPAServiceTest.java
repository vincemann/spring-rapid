package io.github.vincemann.demo.service.springDataJPA.it;

import io.github.vincemann.demo.model.Vet;
import io.github.vincemann.demo.service.springDataJPA.VetJPAService;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.successfulTestBundles.UpdatableSucceedingTestEntityBundle;
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
        super(crudService);
    }

    @Override
    protected List<UpdatableSucceedingTestEntityBundle<Vet>> provideTestEntityBundles() {
        return Arrays.asList(
                new UpdatableSucceedingTestEntityBundle<>(
                        Vet.builder()
                        .firstName("meister")
                        .lastName("yoda")
                        .build()
                )
        );
    }
}
