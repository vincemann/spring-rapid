//package io.github.vincemann.demo.controllers.springAdapter;
//
//
//import io.github.vincemann.demo.config.ServiceConfig;
//import io.github.vincemann.demo.controllers.TestDataInitControllerIntegrationTest;
//import io.github.vincemann.demo.dtos.pet.BasePetDto;
//import io.github.vincemann.demo.dtos.pet.abs.AbstractPetDto;
//import io.github.vincemann.demo.model.Pet;
//import io.github.vincemann.demo.service.PetService;
//import io.github.vincemann.generic.crud.lib.config.TestConfig;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@ActiveProfiles(value = {"test", "springdatajpa"})
//@SpringJUnitWebConfig({TestConfig.class, ServiceConfig.class})
//class PetControllerIT extends TestDataInitControllerIntegrationTest<PetService,Pet> {
//
//    private AbstractPetDto testPetDto;
//    private Pet testPet;
//
//    @MockBean
//    private PetService petService;
//
//
//    @Override
//    @BeforeEach
//    public void setup() throws Exception {
//        super.setup();
//        testPetDto = BasePetDto.builder()
//                .name("esta")
//                .petTypeId(getTestPetType().getId())
//                .build();
//
//        testPet = Pet.builder()
//                .name("esta")
//                .petType(getTestPetType())
//                .build();
//
//
//        //Pet with persisted PetType and persisted Owner
//        testPetDto = BasePetDto.builder()
//                .ownerId(getTestOwner().getId())
//                .petTypeId(getTestPetType().getId())
//                .name("esta")
//                .build();
//
//
//    }
//
//    @Test
//    public void createPet_ShouldSucceed() throws Exception {
//        performCreate(testPet).andExpect(status().isOk());
//    }
//
//
//    @Test
//    public void updatePet_shouldSucceed() throws Exception {
//        //update pets name
//        AbstractPetDto diffPetsNameUpdate = BasePetDto.builder()
//                .name("MODIFIED NAME")
//                .build();
//        Assertions.assertNotEquals(diffPetsNameUpdate.getName(), testPet.getName());
//
//
//        Mockito.when(petService.update(any(),any())).thenReturn()
//        performPartialUpdate(diffPetsNameUpdate)
//                .andExpect(status().isOk());
//
//        Mockito.verify(g)
//    }
//
////    @Test
////    public void updatePet_RemoveOwner_ShouldSucceed() throws Exception {
////        //remove pets owner in update
////        petDtoWithPersistedOwner.setOwnerId(null);
////
////        getUpdateTemplate().updateEntity_ShouldSucceed(petWithPersistedOwner, petDtoWithPersistedOwner,
////                UpdateControllerTestConfiguration.<Pet, Long>Builder()
////                        .fullUpdate(true)
////                        .postUpdateCallback((updated) -> Assertions.assertNull(updated.getOwner()))
////                        .build()
////        );
////    }
//
//    @Test
//    public void createPetWithAlreadySetId_ShouldFail() throws Exception {
//        AbstractPetDto petDtoWithAlreadySetId = BasePetDto.builder()
//                .name("bello")
//                .petTypeId(getTestPetType().getId())
//                .build();
//        petDtoWithAlreadySetId.setId(9L);
//        getCreateTemplate().createEntity_ShouldFail(petDtoWithAlreadySetId);
//    }
//
//
//    @Test
//    public void updatePet_SetPetTypeIdToNull_ShouldFail() throws Exception {
//        //no pettype
//        AbstractPetDto petTypeIdNullUpdate = BasePetDto.builder()
//                .petTypeId(null)
//                .build();
//        getUpdateTemplate().updateEntity_ShouldFail(testPet, petTypeIdNullUpdate,
//                UpdateControllerTestConfiguration.<Pet, Long>Builder()
//                        .fullUpdate(true)
//                        .postUpdateCallback((afterUpdate -> {
//                            Assertions.assertNotNull(afterUpdate.getPetType());
//                            Assertions.assertEquals(testPetDto.getPetTypeId(), afterUpdate.getPetType().getId());
//                        }))
//                        .build()
//        );
//    }
//}
