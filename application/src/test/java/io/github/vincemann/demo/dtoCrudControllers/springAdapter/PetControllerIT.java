package io.github.vincemann.demo.dtoCrudControllers.springAdapter;


import io.github.vincemann.demo.dtoCrudControllers.EntityInitializerControllerIT;
import io.github.vincemann.demo.dtoCrudControllers.PetController;
import io.github.vincemann.demo.dtos.PetDto;
import io.github.vincemann.demo.model.Pet;
import io.github.vincemann.demo.service.PetService;
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
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment =
        SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = {"test", "springdatajpa"})
public class PetControllerIT extends EntityInitializerControllerIT<Pet, PetDto, PetService, PetController> {

    private PetDto petDtoWithPersistedPetType;
    private Pet petWithPersistedPetType;
    private PetDto petDtoWithOwner;
    private Pet petWithOwner;


    public PetControllerIT(@Autowired PetController crudController,
                           @Autowired TestRequestEntityFactory testRequestEntityFactory,
                           @Autowired PlatformTransactionManager transactionManager,
                           @Autowired CheckIfDbDeletedPlugin checkIfDbDeletedPlugin,
                           @Autowired ServiceDeepEqualPlugin serviceDeepEqualPlugin) {
        super(
                crudController,
                testRequestEntityFactory,
                transactionManager,
                checkIfDbDeletedPlugin,
                serviceDeepEqualPlugin
        );
    }

    @Override
    protected void onBeforeProvideEntityBundles() throws Exception {
        super.onBeforeProvideEntityBundles();
        petDtoWithPersistedPetType = PetDto.builder()
                .name("esta")
                .petTypeId(getTestPetType().getId())
                .build();

        petWithPersistedPetType = Pet.builder()
                .name("esta")
                .petType(getTestPetType())
                .build();


        //Pet with persisted PetType and persisted Owner
        petDtoWithOwner = PetDto.builder()
                .ownerId(getTestOwner().getId())
                .petTypeId(getTestPetType().getId())
                .name("esta")
                .build();


        petWithOwner = Pet.builder()
                .owner(getTestOwner())
                .petType(getTestPetType())
                .name("esta")
                .build();

    }

    @Override
    protected List<SuccessfulCreateTestEntityBundle<PetDto>> provideSuccessfulCreateTestEntityBundles() {


        return Arrays.asList(
                new SuccessfulCreateTestEntityBundle<>(petDtoWithPersistedPetType),
                new SuccessfulCreateTestEntityBundle<>(petDtoWithOwner)
        );
    }

    @Override
    public List<UpdateTestEntityBundle<Pet, PetDto>> provideSuccessfulUpdateTestEntityBundles() {
        //update pets name
        PetDto diffPetsNameUpdate = PetDto.builder()
                .name("MODIFIED NAME")
                .petTypeId(getTestPetType().getId())
                .build();

        //remove pets owner in update
        PetDto removePetsOwnerUpdate = PetDto.builder()
                .ownerId(null)
                .petTypeId(getTestPetType().getId())
                .name("esta")
                .build();

        return Arrays.asList(
                new UpdateTestEntityBundle<Pet, PetDto>(petWithPersistedPetType, diffPetsNameUpdate),
                new UpdateTestEntityBundle<Pet, PetDto>(petWithOwner, removePetsOwnerUpdate)

        );
    }

    @Override
    protected List<FailedCreateTestEntityBundle<PetDto>> provideFailingCreateTestBundles() {
        PetDto petDtoWithAlreadySetId = PetDto.builder()
                .name("bello")
                .petTypeId(getTestPetType().getId())
                .build();
        petDtoWithAlreadySetId.setId(42L);
        return Arrays.asList(
                new FailedCreateTestEntityBundle<PetDto>(petDtoWithAlreadySetId)
        );
    }


    @Override
    protected List<UpdateTestEntityBundle<Pet, PetDto>> provideFailedUpdateTestBundles() {
        return Arrays.asList(
                new UpdateTestEntityBundle<Pet, PetDto>(
                        getTestPet(),
                        //no name
                        PetDto.builder()
                                .name(null)
                                .petTypeId(getTestPetType().getId())
                                .build()),
                new UpdateTestEntityBundle<Pet, PetDto>(
                        getTestPet(),
                        //no pettype
                        PetDto.builder()
                                .name("bello")
                                .petTypeId(null)
                                .build()),
                new UpdateTestEntityBundle<Pet, PetDto>(
                        getTestPet(),
                        //invalid OwnerId
                        PetDto.builder()
                                .name("bello")
                                .petTypeId(getTestPetType().getId())
                                .ownerId(-1L)
                                .build())

        );
    }

}
