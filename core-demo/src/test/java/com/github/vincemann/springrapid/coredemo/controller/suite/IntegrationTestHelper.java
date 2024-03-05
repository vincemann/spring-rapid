package com.github.vincemann.springrapid.coredemo.controller.suite;

import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.util.Lists;
import com.github.vincemann.springrapid.coredemo.controller.suite.template.OwnerControllerTestTemplate;
import com.github.vincemann.springrapid.coredemo.controller.suite.template.PetControllerTestTemplate;
import com.github.vincemann.springrapid.coredemo.controller.suite.template.VetControllerTestTemplate;
import com.github.vincemann.springrapid.coredemo.controller.suite.template.VisitControllerTestTemplate;
import com.github.vincemann.springrapid.coredemo.dto.ClinicCardDto;
import com.github.vincemann.springrapid.coredemo.dto.VisitDto;
import com.github.vincemann.springrapid.coredemo.dto.owner.CreateOwnerDto;
import com.github.vincemann.springrapid.coredemo.dto.owner.ReadOwnOwnerDto;
import com.github.vincemann.springrapid.coredemo.model.*;
import com.github.vincemann.springrapid.coredemo.service.*;
import com.github.vincemann.springrapid.coretest.MvcAware;
import com.github.vincemann.springrapid.coretest.TestMethodInitializable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;

@Component
public class IntegrationTestHelper implements TestMethodInitializable, MvcAware {

    @Autowired
    private TestData testData;

    // services

    @Autowired
    protected SpecialtyService specialtyService;
    @Autowired
    protected VetService vetService;
    @Autowired
    protected PetService petService;
    @Autowired
    protected PetTypeService petTypeService;
    @Autowired
    protected VisitService visitService;
    @Autowired
    protected OwnerService ownerService;


    // controllers

    @Autowired
    protected OwnerControllerTestTemplate ownerController;
    @Autowired
    protected PetControllerTestTemplate petController;
    @Autowired
    protected VetControllerTestTemplate vetController;
    @Autowired
    protected VisitControllerTestTemplate visitController;

    @Override
    public void beforeTestMethod() throws BadEntityException {
        testData.initTestData();
        testData.savedDogPetType = petTypeService.create(testData.getSavedDogPetType());
        testData.savedCatPetType = petTypeService.create(testData.getSavedCatPetType());
    }

    @Override
    public void setMvc(MockMvc mvc) {
        ownerController.setMvc(mvc);
        petController.setMvc(mvc);
        vetController.setMvc(mvc);
        visitController.setMvc(mvc);
    }

    public ClinicCardDto saveClinicCardLinkedToOwner(ClinicCard clinicCard, String ownerName) throws Exception {
        ClinicCardDto clinicCardDto = new ClinicCardDto(clinicCard);
        if (ownerName !=null){
            Owner owner = ownerService.findByLastName(ownerName).get();
            clinicCardDto.setOwnerId(owner.getId());
        }
        return ownerController.perform2xxAndDeserialize(ownerController.create(clinicCardDto),ClinicCardDto.class);
    }

    public Visit createVisit(String token, Visit visit, Owner owner, Vet vet, Pet... pets) throws Exception {
        VisitDto dto = new VisitDto(visit);
        dto.setOwnerId(owner.getId());
        dto.setVetId(vet.getId());
        for (Pet pet : pets) {
            dto.getPetIds().add(pet.getId());
        }

        VisitDto response = visitController.perform2xxAndDeserialize(visitController.create(dto)
                        .header(HttpHeaders.AUTHORIZATION,token)
                , VisitDto.class);
        return visitService.findById(response.getId()).get();
    }

    protected ReadOwnOwnerDto saveOwnerLinkedToPets(Owner owner, Long... petIds) throws Exception {
        CreateOwnerDto createOwnerDto = new CreateOwnerDto(owner);
        createOwnerDto.getPetIds().addAll(Lists.newArrayList(petIds));


        return ownerController.perform2xxAndDeserialize(ownerController.create(createOwnerDto),ReadOwnOwnerDto.class);
    }


    protected ReadOwnOwnerDto saveOwnerLinkedToClinicCard(Owner owner, ClinicCard clinicCard) throws Exception {
        CreateOwnerDto createOwnerDto = new CreateOwnerDto(owner);
        createOwnerDto.setClinicCardId(clinicCard.getId());


        return ownerController.perform2xxAndDeserialize(ownerController.create(createOwnerDto),ReadOwnOwnerDto.class);
    }


    protected ReadOwnOwnerDto saveOwner(Owner owner) throws Exception {
        CreateOwnerDto createOwnerDto = new CreateOwnerDto(owner);
        return ownerController.perform2xxAndDeserialize(ownerController.create(createOwnerDto),ReadOwnOwnerDto.class);
    }
}
