package com.github.vincemann.springrapid.acldemo.controller;

import com.github.vincemann.springrapid.acldemo.auth.MyRoles;
import com.github.vincemann.springrapid.acldemo.dto.owner.CreateOwnerDto;
import com.github.vincemann.springrapid.acldemo.dto.owner.FullOwnerDto;
import com.github.vincemann.springrapid.acldemo.dto.pet.FullPetDto;
import com.github.vincemann.springrapid.acldemo.dto.pet.OwnerCreatePetDto;
import com.github.vincemann.springrapid.acldemo.dto.user.UUIDSignupResponseDto;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.model.User;
import com.github.vincemann.springrapid.acldemo.service.OwnerService;
import com.github.vincemann.springrapid.auth.domain.AuthRoles;
import com.github.vincemann.springrapid.auth.domain.dto.SignupDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.github.vincemann.ezcompare.Comparator.compare;
import static com.github.vincemann.springrapid.coretest.service.PropertyMatchers.propertyAssert;


public class OwnerControllerTest extends AbstractControllerIntegrationTest<OwnerController, OwnerService> {




    @Test
    public void canRegisterOwner() throws Exception {
        SignupDto signupDto = SignupDto.builder()
                .email(OWNER_KAHN_EMAIL)
                .password(OWNER_KAHN_PASSWORD)
                .build();
        UUIDSignupResponseDto signedUpDto = perform2xx(userController.signup(signupDto),UUIDSignupResponseDto.class);
        String uuid = signedUpDto.getUuid();
        Assertions.assertNotNull(uuid);

        Optional<User> byUuid = userService.findByUuid(uuid);
        Assertions.assertTrue(byUuid.isPresent());



        CreateOwnerDto createOwnerDto = new CreateOwnerDto(kahn,uuid);
        FullOwnerDto createdDto = perform2xx(create(createOwnerDto),FullOwnerDto.class);

        compare(createdDto).with(createdDto)
                .properties()
                .all().ignore(OwnerType::getId)
                .assertEqual();

        Assertions.assertEquals(FullOwnerDto.DIRTY_SECRET,createdDto.getDirtySecret());

        byUuid = userService.findByUuid(uuid);
        Assertions.assertFalse(byUuid.isPresent());

        Optional<User> kahnUserByEmail = userService.findByEmail(OWNER_KAHN_EMAIL);
        Assertions.assertTrue(kahnUserByEmail.isPresent());
        User dbUserKahn = kahnUserByEmail.get();

        propertyAssert(dbUserKahn)
                .assertContains(dbUserKahn::getRoles, MyRoles.OWNER, AuthRoles.UNVERIFIED, AuthRoles.USER)
                .assertSize(dbUserKahn::getRoles,3)
                .assertEquals(dbUserKahn::getEmail,OWNER_KAHN_EMAIL)
                .assertNotNull(dbUserKahn::getPassword)
                .assertNull(dbUserKahn::getUuid);


        Optional<Owner> kahnByLastName = getService().findByLastName(OWNER_KAHN);
        Assertions.assertTrue(kahnByLastName.isPresent());
        Owner dbKahn = kahnByLastName.get();

        Assertions.assertEquals(dbUserKahn,dbKahn.getUser());

    }

    @Test
    public void canSavePetToOwnAccount() throws Exception {
        Owner dbKahn = registerOwner(kahn, OWNER_KAHN_EMAIL, OWNER_KAHN_PASSWORD);
        getMvc().perform(userController.login(dbKahn.getUser()));
        OwnerCreatePetDto createPetDto = new  OwnerCreatePetDto(bella,dbKahn.getId());
        FullPetDto createdPet = perform2xx(petController.create(createPetDto), FullPetDto.class);
        Assertions.assertEquals(dbKahn.getId(),createdPet.getOwnerId());
        assertOwnerHasPets(OWNER_KAHN,BELLA);
        assertPetHasOwner(BELLA,OWNER_KAHN);

    }





}
