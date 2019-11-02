package io.github.vincemann.demo.controllers.springAdapter;

import io.github.vincemann.demo.controllers.EntityInitializerControllerIT;
import io.github.vincemann.demo.controllers.VetController;
import io.github.vincemann.demo.dtos.VetDto;
import io.github.vincemann.demo.model.Vet;
import io.github.vincemann.demo.service.VetService;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.plugins.CheckIfDbDeletedPlugin;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.plugins.ServiceDeepEqualPlugin;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.create.FailedCreateTestEntityBundle;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.create.SuccessfulCreateTestEntityBundle;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.update.UpdateTestEntityBundle;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.TestRequestEntityFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment =
        SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = {"test", "springdatajpa"})
class VetControllerIT extends EntityInitializerControllerIT<Vet, VetDto, VetService, VetController> {

    private VetDto vetDtoWithoutSpecialty;
    private Vet vetWithoutSpecialty;

    private VetDto vetDtoWithSpecialty;
    private Vet vetWithSpecialty;

    VetControllerIT(@Autowired VetController crudController,
                    @Autowired TestRequestEntityFactory testRequestEntityFactory,
                    @Autowired PlatformTransactionManager platformTransactionManager,
                    @Autowired CheckIfDbDeletedPlugin checkIfDbDeletedPlugin,
                    @Autowired ServiceDeepEqualPlugin serviceDeepEqualPlugin) {
        super(
                crudController,
                testRequestEntityFactory,
                platformTransactionManager,
                checkIfDbDeletedPlugin,
                serviceDeepEqualPlugin
        );
    }

    @Override
    protected void onBeforeProvideEntityBundles() throws Exception {
        super.onBeforeProvideEntityBundles();

        vetDtoWithoutSpecialty = VetDto.builder()
                .firstName("master")
                .lastName("Yoda")
                .build();

        vetWithoutSpecialty= Vet.builder()
                .firstName("master")
                .lastName("Yoda")
                .build();

        vetDtoWithSpecialty= VetDto.builder()
                .firstName("master")
                .lastName("Yoda")
                .specialtyIds(Collections.singleton(getTestSpecialty().getId()))
                .build();
        vetWithSpecialty= Vet.builder()
                .firstName("master")
                .lastName("Yoda")
                .specialties(Collections.singleton(getTestSpecialty()))
                .build();
    }

    @Override
    protected List<SuccessfulCreateTestEntityBundle<VetDto>> provideSuccessfulCreateTestEntityBundles() {
        return Arrays.asList(
                new SuccessfulCreateTestEntityBundle<>(vetDtoWithoutSpecialty),
                new SuccessfulCreateTestEntityBundle<>(vetDtoWithSpecialty)
        );
    }

    @Override
    protected List<UpdateTestEntityBundle<Vet, VetDto>> provideSuccessfulUpdateTestEntityBundles() {
        VetDto diffVetsNameUpdate = VetDto.builder()
                .firstName("UPDATED NAME")
                .lastName("Yoda")
                .specialtyIds(Collections.singleton(getTestSpecialty().getId()))
                .build();
        return Arrays.asList(
            new UpdateTestEntityBundle<Vet, VetDto>(vetWithSpecialty,diffVetsNameUpdate)
        );
    }


    @Override
    protected List<FailedCreateTestEntityBundle<VetDto>> provideFailingCreateTestBundles() {
        return Arrays.asList(
                new FailedCreateTestEntityBundle<>(VetDto.builder()
                        .firstName("master")
                        //no last name
                        //.lastName("Yoda")
                        .build()),
                //Vet with invalid specialty
                new FailedCreateTestEntityBundle<>(VetDto.builder()
                        .firstName("master")
                        .lastName("Yoda")
                        .specialtyIds(Collections.singleton(-1L))
                        .build())
        );
    }

    @Override
    protected List<UpdateTestEntityBundle<Vet, VetDto>> provideFailedUpdateTestBundles() {
        VetDto noNameUpdate = VetDto.builder()
                .firstName(null)
                .lastName("Yoda")
                .build();
        return Arrays.asList(
                new UpdateTestEntityBundle<Vet, VetDto>(vetWithoutSpecialty,noNameUpdate)
        );
    }
}