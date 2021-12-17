package com.github.vincemann.springrapid.coredemo.controller;

import com.github.vincemann.springrapid.coredemo.dtos.ClinicCardDto;
import com.github.vincemann.springrapid.coredemo.model.ClinicCard;
import com.github.vincemann.springrapid.coredemo.model.Owner;
import com.github.vincemann.springrapid.coredemo.service.ClinicCardService;
import com.github.vincemann.springrapid.coretest.util.TransactionalRapidTestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.github.vincemann.ezcompare.Comparator.compare;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ClinicCardControllerTest extends AbstractControllerIntegrationTest<ClinicCardController, ClinicCardService>{


    @Test
    public void canSaveCardWithoutOwner() throws Exception {
        ClinicCardDto createCardDto = new ClinicCardDto(clinicCard);
        ClinicCardDto responseDto = performDs2xx(create(createCardDto), ClinicCardDto.class);


        compare(clinicCard).with(responseDto)
                .properties()
                .all()
                .ignore(ClinicCardType::getId)
                .ignore(ClinicCardType::getOwner)
                .assertEqual();
        Assertions.assertTrue(clinicCardRepository.findById(responseDto.getId()).isPresent());
        assertClinicCardHasOwner(responseDto.getId(),null);
    }

    @Test
    public void canSaveCard_linkToOwner() throws Exception {
        ownerRepository.save(kahn);
        ClinicCardDto responseDto = saveClinicCardLinkedToOwner(clinicCard, KAHN);
        assertClinicCardHasOwner(responseDto.getId(),KAHN);
        assertOwnerHasClinicCard(KAHN,responseDto.getId());
    }

    @Test
    public void canUnlinkOwnerFromCard_viaUpdate() throws Exception {
        ownerRepository.save(kahn);
        ClinicCardDto savedDto = saveClinicCardLinkedToOwner(clinicCard, KAHN);
        String jsonRequest = TransactionalRapidTestUtil.createUpdateJsonRequest(
                TransactionalRapidTestUtil.createUpdateJsonLine("remove", "/ownerId")
        );
        ClinicCardDto responseDto = performDs2xx(update(jsonRequest, savedDto.getId()), ClinicCardDto.class);
        Assertions.assertNull(responseDto.getOwnerId());

        assertClinicCardHasOwner(responseDto.getId(),null);
        assertOwnerHasClinicCard(KAHN,null);
    }

    @Test
    public void canLinkOwnerToCard_viaUpdate() throws Exception {
        Owner savedKahn = ownerRepository.save(kahn);
        ClinicCardDto savedDto = saveClinicCardLinkedToOwner(clinicCard,null);
        String jsonRequest = TransactionalRapidTestUtil.createUpdateJsonRequest(
                TransactionalRapidTestUtil.createUpdateJsonLine("add", "/ownerId",savedKahn.getId().toString())
        );
        ClinicCardDto responseDto = performDs2xx(update(jsonRequest, savedDto.getId()), ClinicCardDto.class);
        Assertions.assertEquals(savedKahn.getId(),responseDto.getOwnerId());

        assertClinicCardHasOwner(responseDto.getId(),KAHN);
        assertOwnerHasClinicCard(KAHN,responseDto.getId());
    }

    @Test
    public void canRelinkDiffOwnerToCard_viaUpdate() throws Exception {
        Owner savedKahn = ownerRepository.save(kahn);
        Owner savedMeier = ownerRepository.save(meier);
        ClinicCardDto savedDto = saveClinicCardLinkedToOwner(clinicCard,KAHN);
        String jsonRequest = TransactionalRapidTestUtil.createUpdateJsonRequest(
                TransactionalRapidTestUtil.createUpdateJsonLine("replace", "/ownerId",savedMeier.getId().toString())
        );
        ClinicCardDto responseDto = performDs2xx(update(jsonRequest, savedDto.getId()), ClinicCardDto.class);
        Assertions.assertEquals(savedMeier.getId(),responseDto.getOwnerId());

        assertClinicCardHasOwner(responseDto.getId(),MEIER);
        assertOwnerHasClinicCard(KAHN,null);
        assertOwnerHasClinicCard(MEIER,responseDto.getId());
    }

    @Test
    public void canRemoveCard_getUnlinkedFromOwner() throws Exception {
        ownerRepository.save(kahn);
        ClinicCardDto responseDto = saveClinicCardLinkedToOwner(clinicCard, KAHN);
        getMvc().perform(delete(responseDto.getId())).andExpect(status().is2xxSuccessful());

        Assertions.assertFalse(clinicCardRepository.findById(responseDto.getId()).isPresent());
        assertOwnerHasClinicCard(KAHN,null);
    }



    private ClinicCardDto saveClinicCardLinkedToOwner(ClinicCard clinicCard,String ownerName) throws Exception {
        ClinicCardDto clinicCardDto = new ClinicCardDto(clinicCard);
        if (ownerName !=null){
            Owner owner = ownerRepository.findByLastName(ownerName).get();
            clinicCardDto.setOwnerId(owner.getId());
        }
        return performDs2xx(create(clinicCardDto),ClinicCardDto.class);
    }

}
