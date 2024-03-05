package com.github.vincemann.springrapid.coredemo.controller;

import com.github.vincemann.springrapid.coredemo.controller.suite.MyIntegrationTest;
import com.github.vincemann.springrapid.coredemo.controller.suite.template.ClinicCardControllerTestTemplate;
import com.github.vincemann.springrapid.coredemo.dto.ClinicCardDto;
import com.github.vincemann.springrapid.coredemo.model.ClinicCard;
import com.github.vincemann.springrapid.coredemo.model.Owner;
import com.github.vincemann.springrapid.coretest.util.RapidTestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.github.vincemann.springrapid.coredemo.controller.suite.TestData.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag(value = "demo-projects")

public class ClinicCardControllerTest extends MyIntegrationTest {

    @Autowired
    ClinicCardControllerTestTemplate controller;

    @Test
    public void canSaveCardWithoutOwner() throws Exception {
        // when
        ClinicCardDto createCardDto = new ClinicCardDto(testData.getClinicCard());
        ClinicCardDto responseDto = controller.perform2xxAndDeserialize(controller.create(createCardDto), ClinicCardDto.class);
        // then
        Assertions.assertNull(responseDto.getOwnerId());
        Assertions.assertTrue(clinicCardService.findById(responseDto.getId()).isPresent());
        assertClinicCardHasOwner(responseDto.getId(),null);
    }

    @Test
    public void canSaveCardLinkedToOwner() throws Exception {
        // given
        Owner kahn = ownerService.create(testData.getKahn());
        // when
        ClinicCardDto responseDto = helper.saveClinicCardLinkedToOwner(testData.getClinicCard(), KAHN);
        // then
        assertClinicCardHasOwner(responseDto.getId(),KAHN);
        assertOwnerHasClinicCard(KAHN,responseDto.getId());
    }

    @Test
    public void canUnlinkOwnerFromCardViaUpdate() throws Exception {
        // given
        Owner kahn = ownerService.create(testData.getKahn());
        ClinicCardDto savedDto = helper.saveClinicCardLinkedToOwner(testData.getClinicCard(), KAHN);
        // when
        String jsonRequest = RapidTestUtil.createUpdateJsonRequest(
                RapidTestUtil.createUpdateJsonLine("remove", "/ownerId")
        );
        ClinicCardDto responseDto = controller.perform2xxAndDeserialize(controller.update(jsonRequest, savedDto.getId()),
                ClinicCardDto.class);
        // then
        Assertions.assertNull(responseDto.getOwnerId());
        assertClinicCardHasOwner(responseDto.getId(),null);
        assertOwnerHasClinicCard(KAHN,null);
    }

    @Test
    public void canLinkOwnerToCardViaUpdate() throws Exception {
        // given
        Owner kahn = ownerService.create(testData.getKahn());
        ClinicCardDto savedDto = helper.saveClinicCardLinkedToOwner(testData.getClinicCard(),null);
        // when
        String jsonRequest = RapidTestUtil.createUpdateJsonRequest(
                RapidTestUtil.createUpdateJsonLine("add", "/ownerId",kahn.getId().toString())
        );
        ClinicCardDto responseDto = controller.perform2xxAndDeserialize(controller.update(jsonRequest, savedDto.getId()),
                ClinicCardDto.class);
        // then
        Assertions.assertEquals(kahn.getId(),responseDto.getOwnerId());
        assertClinicCardHasOwner(responseDto.getId(),KAHN);
        assertOwnerHasClinicCard(KAHN,responseDto.getId());
    }

    @Test
    public void canRelinkDiffOwnerToCardViaUpdate() throws Exception {
        // given
        Owner kahn = ownerService.create(testData.getKahn());
        Owner meier = ownerService.create(testData.getMeier());
        ClinicCardDto savedDto = helper.saveClinicCardLinkedToOwner(testData.getClinicCard(),KAHN);
        // when
        String jsonRequest = RapidTestUtil.createUpdateJsonRequest(
                RapidTestUtil.createUpdateJsonLine("replace", "/ownerId",meier.getId().toString())
        );
        ClinicCardDto responseDto = controller.perform2xxAndDeserialize(controller.update(jsonRequest, savedDto.getId()),
                ClinicCardDto.class);
        // then
        Assertions.assertEquals(meier.getId(),responseDto.getOwnerId());
        assertClinicCardHasOwner(responseDto.getId(),MEIER);
        assertOwnerHasClinicCard(KAHN,null);
        assertOwnerHasClinicCard(MEIER,responseDto.getId());
    }

    @Test
    public void givenOwnerLinkedToCard_whenRemovingCard_thenCardGetsUnlinkedFromOwner() throws Exception {
        // given
        Owner kahn = ownerService.create(testData.getKahn());
        ClinicCardDto responseDto = helper.saveClinicCardLinkedToOwner(testData.getClinicCard(), KAHN);
        // when
        getMvc().perform(controller.delete(responseDto.getId())).andExpect(status().is2xxSuccessful());
        // then
        Assertions.assertFalse(clinicCardService.findById(responseDto.getId()).isPresent());
        assertOwnerHasClinicCard(KAHN,null);
    }


}
