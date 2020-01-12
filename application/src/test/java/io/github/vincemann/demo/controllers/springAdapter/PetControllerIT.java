package io.github.vincemann.demo.controllers.springAdapter;


import io.github.vincemann.demo.controllers.EntityInitializer_ControllerIT;
import io.github.vincemann.demo.dtos.PetDto;
import io.github.vincemann.demo.model.Pet;
import io.github.vincemann.demo.repositories.PetRepository;
import io.github.vincemann.generic.crud.lib.test.postUpdateCallback.PostUpdateCallback;
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
class PetControllerIT
        extends EntityInitializer_ControllerIT<Pet,PetRepository> {

    private PetDto petDtoWithPersistedPetType;
    private Pet petWithPersistedPetType;
    private PetDto petDtoWithPersistedOwner;
    private Pet petWithPersistedOwner;


    @Override
    @BeforeEach
    public void setup() throws Exception {
        super.setup();
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
                .build();
        Assertions.assertNotEquals(diffPetsNameUpdate.getName(),petWithPersistedPetType.getName());
        updateEntity_ShouldSucceed(petWithPersistedPetType, diffPetsNameUpdate,false,new PostUpdateCallback<Pet,Long>() {
            @Override
            public void callback(Pet after) {
                Assertions.assertEquals(diffPetsNameUpdate.getName(), after.getName());
            }
        });
    }

    @Test
    public void updatePet_RemoveOwner_ShouldSucceed() throws Exception {
        //remove pets owner in update
        petDtoWithPersistedOwner.setOwnerId(null);

        updateEntity_ShouldSucceed(petWithPersistedOwner, petDtoWithPersistedOwner,true, new PostUpdateCallback<Pet,Long>() {
            @Override
            public void callback(Pet after) {
                Assertions.assertNull(after.getOwner());
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
                .petTypeId(null)
                .build();
        updateEntity_ShouldFail(petWithPersistedPetType, petTypeIdNullUpdate,true, new PostUpdateCallback<Pet,Long>() {
            @Override
            public void callback(Pet after) {
                Assertions.assertNotNull(after.getPetType());
                Assertions.assertEquals(petDtoWithPersistedPetType.getPetTypeId(), after.getPetType().getId());
            }
        });
    }
}
