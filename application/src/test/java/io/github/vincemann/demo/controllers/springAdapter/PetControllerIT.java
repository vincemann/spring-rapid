package io.github.vincemann.demo.controllers.springAdapter;


import io.github.vincemann.demo.controllers.MyControllerIntegrationTest;
import io.github.vincemann.demo.dtos.pet.BasePetDto;
import io.github.vincemann.demo.dtos.pet.abs.AbstractPetDto;
import io.github.vincemann.demo.model.Pet;
import io.github.vincemann.generic.crud.lib.test.controller.crudTests.config.UpdateControllerTestConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment =
        SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles(value = {"test", "springdatajpa"})
class PetControllerIT extends MyControllerIntegrationTest<Pet> {

    private AbstractPetDto petDtoWithPersistedPetType;
    private Pet petWithPersistedPetType;
    private AbstractPetDto petDtoWithPersistedOwner;
    private Pet petWithPersistedOwner;


    @Override
    @BeforeEach
    public void setup() throws Exception {
        super.setup();
        petDtoWithPersistedPetType = BasePetDto.builder()
                .name("esta")
                .petTypeId(getTestPetType().getId())
                .build();

        petWithPersistedPetType = Pet.builder()
                .name("esta")
                .petType(getTestPetType())
                .build();


        //Pet with persisted PetType and persisted Owner
        petDtoWithPersistedOwner = BasePetDto.builder()
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
        getCreateTemplate().createEntity_ShouldSucceed(petDtoWithPersistedPetType);
    }

    @Test
    public void createPetWithPersistedOwner_ShouldSucceed() throws Exception {
        getCreateTemplate().createEntity_ShouldSucceed(petDtoWithPersistedOwner);
    }

    @Test
    public void updatePetsName_ShouldSucceed() throws Exception {
        //update pets name
        AbstractPetDto diffPetsNameUpdate = BasePetDto.builder()
                .name("MODIFIED NAME")
                .build();
        Assertions.assertNotEquals(diffPetsNameUpdate.getName(), petWithPersistedPetType.getName());
        getUpdateTemplate().updateEntity_ShouldSucceed(petWithPersistedPetType, diffPetsNameUpdate,
                UpdateControllerTestConfiguration.<Pet, Long>Builder()
                        .fullUpdate(false)
                        .postUpdateCallback((updated) -> {
                            Assertions.assertEquals(diffPetsNameUpdate.getName(), updated.getName());
                        })
                        .build()
        );
    }

    @Test
    public void updatePet_RemoveOwner_ShouldSucceed() throws Exception {
        //remove pets owner in update
        petDtoWithPersistedOwner.setOwnerId(null);

        getUpdateTemplate().updateEntity_ShouldSucceed(petWithPersistedOwner, petDtoWithPersistedOwner,
                UpdateControllerTestConfiguration.<Pet, Long>Builder()
                        .fullUpdate(true)
                        .postUpdateCallback((updated) -> Assertions.assertNull(updated.getOwner()))
                        .build()
        );
    }

    @Test
    public void createPetWithAlreadySetId_ShouldFail() throws Exception {
        AbstractPetDto petDtoWithAlreadySetId = BasePetDto.builder()
                .name("bello")
                .petTypeId(getTestPetType().getId())
                .build();
        petDtoWithAlreadySetId.setId(9L);
        getCreateTemplate().createEntity_ShouldFail(petDtoWithAlreadySetId);
    }


    @Test
    public void updatePet_SetPetTypeIdToNull_ShouldFail() throws Exception {
        //no pettype
        AbstractPetDto petTypeIdNullUpdate = BasePetDto.builder()
                .petTypeId(null)
                .build();
        getUpdateTemplate().updateEntity_ShouldFail(petWithPersistedPetType, petTypeIdNullUpdate,
                UpdateControllerTestConfiguration.<Pet, Long>Builder()
                        .fullUpdate(true)
                        .postUpdateCallback((afterUpdate -> {
                            Assertions.assertNotNull(afterUpdate.getPetType());
                            Assertions.assertEquals(petDtoWithPersistedPetType.getPetTypeId(), afterUpdate.getPetType().getId());
                        }))
                        .build()
        );
    }
}
