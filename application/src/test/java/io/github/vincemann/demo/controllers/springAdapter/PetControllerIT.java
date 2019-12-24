package io.github.vincemann.demo.controllers.springAdapter;


import io.github.vincemann.demo.controllers.EntityInitializer_ControllerIT;
import io.github.vincemann.demo.controllers.PetController;
import io.github.vincemann.demo.dtos.PetDto;
import io.github.vincemann.demo.model.Pet;
import io.github.vincemann.demo.repositories.PetRepository;
import io.github.vincemann.demo.service.PetService;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.plugins.DatabaseDeletedCheck_Plugin;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.postUpdateCallback.PostUpdateCallback;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.TestRequestEntity_Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment =
        SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles(value = {"test", "springdatajpa"})
class PetControllerIT
        extends EntityInitializer_ControllerIT<Pet, PetDto, PetRepository, PetService, PetController> {

    private PetDto petDtoWithPersistedPetType;
    private Pet petWithPersistedPetType;
    private PetDto petDtoWithPersistedOwner;
    private Pet petWithPersistedOwner;


    public PetControllerIT(
                           @Autowired DatabaseDeletedCheck_Plugin databaseDeletedCheckPlugin) {
        super(
                databaseDeletedCheckPlugin
        );
    }

    @Override
    public void beforeEachTest() throws Exception {
        super.beforeEachTest();
        petDtoWithPersistedPetType = PetDto.builder()
                .name("esta")
                .petTypeId(getTestPetType().getId())
                .build();

        petWithPersistedPetType = Pet.builder()
                .name("esta")
                .petType(getTestPetType())
                .build();


        //Pet with persisted PetType and persisted Owner
        petDtoWithPersistedOwner = PetDto.builder()
                .ownerId(getTestOwner().getId())
                .petTypeId(getTestPetType().getId())
                .name("esta")
                .build();


        petWithPersistedOwner = Pet.builder()
                .owner(getTestOwner())
                .petType(getTestPetType())
                .name("esta")
                .build();
    }


    @Test
    public void createPetWithPersistedPetType_ShouldSucceed() throws Exception {
        createEntity_ShouldSucceed(petDtoWithPersistedPetType);
    }

    @Test
    public void createPetWithPersistedOwner_ShouldSucceed() throws Exception {
        createEntity_ShouldSucceed(petDtoWithPersistedOwner);
    }

    @Test
    public void updatePetsName_ShouldSucceed() throws Exception {
        //update pets name
        PetDto diffPetsNameUpdate = PetDto.builder()
                .name("MODIFIED NAME")
                .petTypeId(getTestPetType().getId())
                .build();
        Assertions.assertNotEquals(diffPetsNameUpdate.getName(),petWithPersistedPetType.getName());
        updateEntity_ShouldSucceed(petWithPersistedPetType, diffPetsNameUpdate, new PostUpdateCallback<Pet>() {
            @Override
            public void callback(Pet entityToUpdate, Pet updatedEntity) {
                Assertions.assertEquals(diffPetsNameUpdate.getName(),updatedEntity.getName());
            }
        });
    }

    @Test
    public void updatePet_RemoveOwner_ShouldSucceed() throws Exception {
        //remove pets owner in update
        PetDto removePetsOwnerUpdate = PetDto.builder()
                .ownerId(null)
                .petTypeId(getTestPetType().getId())
                .name("esta")
                .build();

        updateEntity_ShouldSucceed(petWithPersistedOwner, removePetsOwnerUpdate, new PostUpdateCallback<Pet>() {
            @Override
            public void callback(Pet entityToUpdate, Pet updatedEntity) {
                Assertions.assertNull(updatedEntity.getOwner());
            }
        });
    }

    @Test
    public void createPetWithAlreadySetId_ShouldFail() throws Exception {
        PetDto petDtoWithAlreadySetId = PetDto.builder()
                .name("bello")
                .petTypeId(getTestPetType().getId())
                .build();
        petDtoWithAlreadySetId.setId(9L);
        createEntity_ShouldFail(petDtoWithAlreadySetId);

    }



    @Test
    public void updatePet_SetPetTypeIdToNull_ShouldFail() throws Exception {
        //no pettype
        PetDto petTypeIdNullUpdate = PetDto.builder()
                .name("bello")
                .petTypeId(null)
                .build();
        updateEntity_ShouldFail(petWithPersistedPetType, petTypeIdNullUpdate, new PostUpdateCallback<Pet>() {
            @Override
            public void callback(Pet entityToUpdate, Pet updatedEntity) {
                Assertions.assertNotNull(updatedEntity.getPetType());
                Assertions.assertEquals(petDtoWithPersistedPetType.getPetTypeId(),updatedEntity.getPetType().getId());
            }
        });
    }
}
