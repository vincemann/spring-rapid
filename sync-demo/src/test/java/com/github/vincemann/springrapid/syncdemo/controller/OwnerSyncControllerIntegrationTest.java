package com.github.vincemann.springrapid.syncdemo.controller;

import com.fasterxml.jackson.databind.type.CollectionType;
import com.github.vincemann.springrapid.core.Entity;
import com.github.vincemann.springrapid.core.controller.AbstractEntityController;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import com.github.vincemann.springrapid.core.service.filter.jpa.ParentFilter;
import com.github.vincemann.springrapid.core.service.filter.jpa.QueryFilter;
import com.github.vincemann.springrapid.core.util.Lists;
import com.github.vincemann.springrapid.coretest.TestPrincipal;
import com.github.vincemann.springrapid.coretest.controller.UrlExtension;
import com.github.vincemann.springrapid.coretest.util.RapidTestUtil;
import com.github.vincemann.springrapid.sync.controller.EntitySyncStatusSerializer;
import com.github.vincemann.springrapid.sync.model.EntityLastUpdateInfo;
import com.github.vincemann.springrapid.sync.model.EntitySyncStatus;
import com.github.vincemann.springrapid.sync.model.SyncStatus;
import com.github.vincemann.springrapid.syncdemo.controller.sync.OwnerSyncController;
import com.github.vincemann.springrapid.syncdemo.controller.sync.PetSyncController;
import com.github.vincemann.springrapid.syncdemo.dto.owner.ReadOwnOwnerDto;
import com.github.vincemann.springrapid.syncdemo.dto.pet.PetDto;
import com.github.vincemann.springrapid.syncdemo.model.ClinicCard;
import com.github.vincemann.springrapid.syncdemo.model.Owner;
import com.github.vincemann.springrapid.syncdemo.model.Pet;
import com.github.vincemann.springrapid.syncdemo.service.OwnerService;
import com.github.vincemann.springrapid.syncdemo.service.filter.OwnerTelNumberFilter;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OwnerSyncControllerIntegrationTest extends AbstractControllerIntegrationTest<OwnerController, OwnerService>{

    @Autowired
    OwnerSyncController ownerSyncController;

    @Autowired
    PetSyncController petSyncController;

    @Autowired
    EntitySyncStatusSerializer syncStatusSerializer;

    @Autowired
    PetController petController;


    @Autowired
    ApplicationContext applicationContext;


    @Test
    public void checkSyncStatus_whenLastClientUpdateBeforeEntityUpdate() throws Exception {
        // create owner
        // record client ts as now - 1 hour -> far before creation of owner
        // check if owner was updated -> yes
        Owner owner = saveOwnerLinkedToPets(kahn);

        Timestamp lastServerUpdate = new Timestamp(owner.getLastModifiedDate().getTime());

        // Subtract one hour (3600,000 milliseconds) from the current timestamp
        long oneHourInMillis = 3600_000;
        long newTimestampInMillis = lastServerUpdate.getTime() - oneHourInMillis;

        // Create a new Timestamp from the adjusted milliseconds value
        Timestamp oneHourBeforeServerUpdate = new Timestamp(newTimestampInMillis);

        Assertions.assertTrue(oneHourBeforeServerUpdate.before(lastServerUpdate));

        fetchOwnerSyncStatus_assertUpdate(owner.getId(),oneHourBeforeServerUpdate,SyncStatus.UPDATED);
    }

    @Test
    public void checkSyncStatus_whenLastClientUpdateAfterEntityUpdate() throws Exception {
        // create owner
        // record client ts as now + 1 hour -> far after creation of owner
        // check if owner was updated -> no
        Owner owner = fetchOwner(saveOwnerLinkedToPets(kahn).getId());

        Timestamp lastServerUpdate = new Timestamp(owner.getLastModifiedDate().getTime());

        // Subtract one hour (3600,000 milliseconds) from the current timestamp
        long oneHourInMillis = 3600_000;
        long newTimestampInMillis = lastServerUpdate.getTime() + oneHourInMillis;

        // Create a new Timestamp from the adjusted milliseconds value
        Timestamp oneHourAfterServerUpdate = new Timestamp(newTimestampInMillis);

        Assertions.assertTrue(oneHourAfterServerUpdate.after(lastServerUpdate));

        fetchOwnerSyncStatus_assertNoUpdate(owner.getId(),oneHourAfterServerUpdate);
    }

    @Test
    public void lastUpdateNow_checkSyncStatus_noUpdateRequired() throws Exception {
        // create owner
        // record client ts as now
        // check if owner was updated -> no
        Owner owner = fetchOwner(saveOwnerLinkedToPets(kahn).getId());

        Timestamp lastServerUpdate = new Timestamp(owner.getLastModifiedDate().getTime());

        // now
        Timestamp now = new Timestamp(new Date().getTime());

        Assertions.assertTrue(now.after(lastServerUpdate));

        fetchOwnerSyncStatus_assertNoUpdate(owner.getId(),now);
    }

    @Test
    public void checkSyncStatus_noSyncNeeded_updateEntity_checkSyncStatus_updateRequired_fetchUpdate() throws Exception {
        // create owner
        // record client ts as now
        // check if owner was updated -> no
        // update owner's first name
        // check if owner was updated -> yes
        // fetch owner with id from sync info and validate first name changed

        Owner owner = saveOwnerLinkedToPets(kahn);

        Timestamp lastServerUpdate = new Timestamp(owner.getLastModifiedDate().getTime());

        // now
        Timestamp clientUpdate = new Timestamp(new Date().getTime());
        Assertions.assertTrue(clientUpdate.after(lastServerUpdate));


        fetchOwnerSyncStatus_assertNoUpdate(owner.getId(),clientUpdate);


        String updatedFirstName = owner.getFirstName()+"-updated";
        Owner updateOwner = Entity.createUpdate(owner);
        updateOwner.setFirstName(updatedFirstName);

        Owner updated = ownerService.partialUpdate(updateOwner);
        Assertions.assertEquals(updatedFirstName,updated.getFirstName());
        Assertions.assertTrue(updated.getLastModifiedDate().after(lastServerUpdate));
        Assertions.assertTrue(clientUpdate.before(updated.getLastModifiedDate()));

        // now should need update
        EntitySyncStatus status = fetchOwnerSyncStatus_assertUpdate(owner.getId(), clientUpdate, SyncStatus.UPDATED);

        securityContext.login(TestPrincipal.withName(KAHN));
        ReadOwnOwnerDto updatedEG = performDs2xx(testTemplate.find(status.getId())
                , ReadOwnOwnerDto.class);
        RapidSecurityContext.logout();

        Assertions.assertEquals(updatedFirstName,updatedEG.getFirstName());
    }


    @Test
    public void checkSyncStatusForAllOwners_sinceTimestamp_findUpdates() throws Exception {
        // create 3 owners
        // record client ts
        // modify 2
        // ask server for updateInfos of all owner's
        // server should tell client about the two updates of owner2 and owner3
        // query server for those updates and validate
        Owner owner = saveOwnerLinkedToPets(kahn);
        Owner owner2 = saveOwnerLinkedToPets(meier);
        Owner owner3 = saveOwnerLinkedToPets(gil);

        Timestamp lastServerUpdate = new Timestamp(owner.getLastModifiedDate().getTime());

        // now
        Timestamp clientUpdate = new Timestamp(new Date().getTime());
        Assertions.assertTrue(clientUpdate.after(lastServerUpdate));

        fetchOwnerSyncStatusesSinceTs_assertNoUpdates(clientUpdate);

        // update owner 2 and 3
        Owner updateOwner2 = Entity.createUpdate(owner2);
        updateOwner2.setCity(owner2.getCity() + "updated");


        Owner updateOwner3 = Entity.createUpdate(owner3);
        updateOwner3.setCity(owner3.getCity() + "updated");

        Owner updatedOwner2 = ownerService.partialUpdate(updateOwner2);
        Owner updatedOwner3 = ownerService.partialUpdate(updateOwner3);

        Assertions.assertTrue(updatedOwner2.getLastModifiedDate().after(lastServerUpdate));
        Assertions.assertTrue(updatedOwner3.getLastModifiedDate().after(lastServerUpdate));

        // now should need update for owner2 and owner3
        Set<EntitySyncStatus> statuses = fetchOwnerSyncStatusesSinceTs_assertUpdates(clientUpdate);
        Assertions.assertEquals(2,statuses.size());
        EntitySyncStatus owner2SyncStatus = statuses.stream().filter(s -> s.getId().equals(updatedOwner2.getId().toString())).findFirst().get();
        EntitySyncStatus owner3SyncStatus = statuses.stream().filter(s -> s.getId().equals(updatedOwner3.getId().toString())).findFirst().get();


        Assertions.assertEquals(owner2SyncStatus.getStatus(), SyncStatus.UPDATED);
        Assertions.assertEquals(owner3SyncStatus.getStatus(), SyncStatus.UPDATED);

        Set<String> idsToSync = Sets.newHashSet(owner2SyncStatus.getId(),owner3SyncStatus.getId());

        securityContext.login(TestPrincipal.withName(KAHN));
        String json = perform(post(getFindSomeUrl())
                .content(getController().getJsonMapper().writeDto(idsToSync))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString();
        RapidSecurityContext.logout();

        CollectionType ownerSetType = getController().getJsonMapper().getObjectMapper()
                .getTypeFactory().constructCollectionType(Set.class, ReadOwnOwnerDto.class);
        Set<ReadOwnOwnerDto> updatedOwners = deserialize(json, ownerSetType);

        Assertions.assertEquals(2,updatedOwners.size());

        ReadOwnOwnerDto owner2Dto = updatedOwners.stream().filter(s -> s.getId().equals(updatedOwner2.getId())).findFirst().get();
        ReadOwnOwnerDto owner3Dto = updatedOwners.stream().filter(s -> s.getId().equals(updatedOwner3.getId())).findFirst().get();

        Assertions.assertEquals(owner2Dto.getCity(),updateOwner2.getCity());
        Assertions.assertEquals(owner3Dto.getCity(),updateOwner3.getCity());
    }

    @Test
    public void checkSyncStatusForSomeOwners_withGivenEntityUpdateInfos() throws Exception {
        // create 3 owners
        // record client ts
        // modify owner2 and owner3
        // ask server for sync info of owner2 and owner1
        // server should tell client about update of owner2
        // query server for those updates and validate
        Owner owner = saveOwnerLinkedToPets(kahn);
        Owner owner2 = saveOwnerLinkedToPets(meier);
        Owner owner3 = saveOwnerLinkedToPets(gil);

        Timestamp lastServerUpdate = new Timestamp(owner.getLastModifiedDate().getTime());

        // now
        Timestamp clientUpdate = new Timestamp(new Date().getTime());
        Assertions.assertTrue(clientUpdate.after(lastServerUpdate));

        fetchOwnerSyncStatusesSinceTs_assertNoUpdates(clientUpdate);

        // update owner 2 and 3
        Owner updateOwner2 = Entity.createUpdate(owner2);
        updateOwner2.setCity(owner2.getCity() + "updated");


        Owner updateOwner3 = Entity.createUpdate(owner3);
        updateOwner3.setCity(owner3.getCity() + "updated");

        Owner updatedOwner2 = ownerService.partialUpdate(updateOwner2);
        Owner updatedOwner3 = ownerService.partialUpdate(updateOwner3);

        Assertions.assertTrue(updatedOwner2.getLastModifiedDate().after(lastServerUpdate));
        Assertions.assertTrue(updatedOwner3.getLastModifiedDate().after(lastServerUpdate));

        // now should ask for update info for owner2 and owner3
        Set<EntityLastUpdateInfo> lastUpdateInfos = new HashSet<>();
        lastUpdateInfos.add(new EntityLastUpdateInfo(owner.getId(),clientUpdate));
        lastUpdateInfos.add(new EntityLastUpdateInfo(owner2.getId(),clientUpdate));
        Set<EntitySyncStatus> statuses = fetchOwnerSyncStatuses_assertUpdates(lastUpdateInfos);
        Assertions.assertEquals(1,statuses.size());
        EntitySyncStatus owner2SyncStatus = statuses.stream().filter(s -> s.getId().equals(updatedOwner2.getId().toString())).findFirst().get();

        Assertions.assertEquals(owner2SyncStatus.getStatus(), SyncStatus.UPDATED);

        Set<String> idsToSync = Sets.newHashSet(owner2SyncStatus.getId());

        securityContext.login(TestPrincipal.withName(KAHN));
        String json = perform(post(getFindSomeUrl())
                .content(getController().getJsonMapper().writeDto(idsToSync))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString();
        RapidSecurityContext.logout();

        CollectionType ownerSetType = getController().getJsonMapper().getObjectMapper()
                .getTypeFactory().constructCollectionType(Set.class, ReadOwnOwnerDto.class);
        Set<ReadOwnOwnerDto> updatedOwners = deserialize(json, ownerSetType);

        Assertions.assertEquals(1,updatedOwners.size());

        ReadOwnOwnerDto owner2Dto = updatedOwners.stream().filter(s -> s.getId().equals(updatedOwner2.getId())).findFirst().get();

        Assertions.assertEquals(owner2Dto.getCity(),updateOwner2.getCity());
    }

    @Test
    public void checkSyncStatusForSomeOwners_withGivenEntityUpdateInfos_withDifferentLastUpdateTimestamps() throws Exception {
        // create 3 owners
        // record client ts1
        // modify owner2 and owner3
        // record client ts2
        // ask server for sync info of owner2:ts1 and owner3:ts2
        // server should tell client about update of owner2 (owner3 ts is after last update -> no update required)
        // query server for those updates and validate
        Owner owner = saveOwnerLinkedToPets(kahn);
        Owner owner2 = saveOwnerLinkedToPets(meier);
        Owner owner3 = saveOwnerLinkedToPets(gil);

        Timestamp lastServerUpdate = new Timestamp(owner.getLastModifiedDate().getTime());

        // now
        Timestamp clientUpdate1 = new Timestamp(new Date().getTime());
        Assertions.assertTrue(clientUpdate1.after(lastServerUpdate));

        fetchOwnerSyncStatusesSinceTs_assertNoUpdates(clientUpdate1);

        // update owner 2 and 3
        Owner updateOwner2 = Entity.createUpdate(owner2);
        updateOwner2.setCity(owner2.getCity() + "updated");


        Owner updateOwner3 = Entity.createUpdate(owner3);
        updateOwner3.setCity(owner3.getCity() + "updated");

        Owner updatedOwner2 = ownerService.partialUpdate(updateOwner2);
        Owner updatedOwner3 = ownerService.partialUpdate(updateOwner3);

        Timestamp clientUpdate2 = new Timestamp(new Date().getTime());

        Assertions.assertTrue(updatedOwner2.getLastModifiedDate().after(lastServerUpdate));
        Assertions.assertTrue(updatedOwner3.getLastModifiedDate().after(lastServerUpdate));
        Assertions.assertTrue(updatedOwner2.getLastModifiedDate().before(clientUpdate2));
        Assertions.assertTrue(updatedOwner3.getLastModifiedDate().before(clientUpdate2));

        // now should ask for update info for owner2 and owner3
        Set<EntityLastUpdateInfo> lastUpdateInfos = new HashSet<>();
        lastUpdateInfos.add(new EntityLastUpdateInfo(owner2.getId(),clientUpdate1));
        lastUpdateInfos.add(new EntityLastUpdateInfo(owner3.getId(),clientUpdate2));
        Set<EntitySyncStatus> statuses = fetchOwnerSyncStatuses_assertUpdates(lastUpdateInfos);
        Assertions.assertEquals(1,statuses.size());
        EntitySyncStatus owner2SyncStatus = statuses.stream().filter(s -> s.getId().equals(updatedOwner2.getId().toString())).findFirst().get();

        Assertions.assertEquals(owner2SyncStatus.getStatus(), SyncStatus.UPDATED);

        Set<String> idsToSync = Sets.newHashSet(owner2SyncStatus.getId());

        securityContext.login(TestPrincipal.withName(KAHN));
        String json = perform(post(getFindSomeUrl())
                .content(getController().getJsonMapper().writeDto(idsToSync))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString();
        RapidSecurityContext.logout();

        CollectionType ownerSetType = getController().getJsonMapper().getObjectMapper()
                .getTypeFactory().constructCollectionType(Set.class, ReadOwnOwnerDto.class);
        Set<ReadOwnOwnerDto> updatedOwners = deserialize(json, ownerSetType);

        Assertions.assertEquals(1,updatedOwners.size());

        ReadOwnOwnerDto owner2Dto = updatedOwners.stream().filter(s -> s.getId().equals(updatedOwner2.getId())).findFirst().get();

        Assertions.assertEquals(owner2Dto.getCity(),updateOwner2.getCity());
    }

    @Test
    public void checkSyncStatusForFilteredOwners_sinceTimestamp() throws Exception {
        // create 3 owners
        // record client ts
        // modify owner2 and owner3
        // ask server for sync infos of all owners, that match filter OwnerTelNumberFilter - that have telnr that starts with 0176 -> owner3 and owner1
        // server should tell client about update of owner3 (owner2 was also updated, but did not match filter)
        // query server for those updates and validate
        Owner owner = saveOwnerLinkedToPets(kahn);
        Owner owner2 = saveOwnerLinkedToPets(meier);
        Owner owner3 = saveOwnerLinkedToPets(gil);

        Timestamp lastServerUpdate = new Timestamp(owner.getLastModifiedDate().getTime());

        // now
        Timestamp clientUpdate = new Timestamp(new Date().getTime());
        Assertions.assertTrue(clientUpdate.after(lastServerUpdate));

        fetchOwnerSyncStatusesSinceTs_assertNoUpdates(clientUpdate);

        // update owner 2 and 3
        Owner updateOwner2 = Entity.createUpdate(owner2);
        updateOwner2.setCity(owner2.getCity() + "updated");


        Owner updateOwner3 = Entity.createUpdate(owner3);
        updateOwner3.setCity(owner3.getCity() + "updated");

        Owner updatedOwner2 = ownerService.partialUpdate(updateOwner2);
        Owner updatedOwner3 = ownerService.partialUpdate(updateOwner3);

        Assertions.assertTrue(updatedOwner2.getLastModifiedDate().after(lastServerUpdate));
        Assertions.assertTrue(updatedOwner3.getLastModifiedDate().after(lastServerUpdate));

        // now should ask for update info for owner2 and owner3
        UrlExtension telPrefixFilter0176 = new UrlExtension(OwnerTelNumberFilter.class,"0176");
        Set<EntitySyncStatus> statuses = fetchOwnerSyncStatusesSinceTs_assertUpdates(clientUpdate,telPrefixFilter0176);
        Assertions.assertEquals(1,statuses.size());
        EntitySyncStatus owner3SyncStatus = statuses.stream().filter(s -> s.getId().equals(updatedOwner3.getId().toString())).findFirst().get();

        Assertions.assertEquals(owner3SyncStatus.getStatus(), SyncStatus.UPDATED);

        Set<String> idsToSync = Sets.newHashSet(owner3SyncStatus.getId());

        securityContext.login(TestPrincipal.withName(KAHN));
        String json = perform(post(getFindSomeUrl())
                .content(getController().getJsonMapper().writeDto(idsToSync))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString();
        RapidSecurityContext.logout();

        CollectionType ownerSetType = getController().getJsonMapper().getObjectMapper()
                .getTypeFactory().constructCollectionType(Set.class, ReadOwnOwnerDto.class);
        Set<ReadOwnOwnerDto> updatedOwners = deserialize(json, ownerSetType);

        Assertions.assertEquals(1,updatedOwners.size());

        ReadOwnOwnerDto owner3Dto = updatedOwners.stream().filter(s -> s.getId().equals(updatedOwner3.getId())).findFirst().get();
        Assertions.assertEquals(owner3Dto.getCity(),updateOwner3.getCity());
    }

    @Test
    public void checkSyncStatusForAllPetsOfOwner_sinceTimestamp() throws Exception {
        // create 2 owners
        // owner 1 has bello and kitty
        // owner 2 has bella
        // record client ts
        // modify bello and bella
        // ask server for sync infos of all pets of owner1 ( via filter)
        // server should tell client about update of bello but not about bella
        // query server for those updates and validate
        Pet savedBello = petRepository.save(bello);
        Pet savedBella = petRepository.save(bella);
        Pet savedKitty = petRepository.save(kitty);
        Owner owner = saveOwnerLinkedToPets(kahn,savedBello.getId(),savedKitty.getId());
        Owner owner2 = saveOwnerLinkedToPets(meier,savedBella.getId());

        Timestamp lastServerUpdate = new Timestamp(owner.getLastModifiedDate().getTime());

        // now
        Timestamp clientUpdate = new Timestamp(new Date().getTime());
        Assertions.assertTrue(clientUpdate.after(lastServerUpdate));

        fetchOwnerSyncStatusesSinceTs_assertNoUpdates(clientUpdate);

        // update bello and bella
        Pet updateBello = Entity.createUpdate(bello);
        updateBello.setBirthDate(savedBello.getBirthDate().minusDays(3));


        Pet updateBella = Entity.createUpdate(bella);
        updateBella.setBirthDate(savedBella.getBirthDate().minusDays(2));

        Pet updatedBello = petService.partialUpdate(updateBello);
        Pet updatedBella = petService.partialUpdate(updateBella);

        Assertions.assertTrue(updatedBello.getLastModifiedDate().after(lastServerUpdate));
        Assertions.assertTrue(updatedBella.getLastModifiedDate().after(lastServerUpdate));

        // now should ask for update infos for all pets with owner=kahn since clientUpdate ts
        UrlExtension parentFilter = new UrlExtension(ParentFilter.class,"owner",owner.getId().toString());
        Set<EntitySyncStatus> statuses = fetchPetSyncStatusesSinceTs_assertUpdates(clientUpdate,parentFilter);

        Assertions.assertEquals(1,statuses.size());
        EntitySyncStatus belloSyncStatus = statuses.stream().filter(s -> s.getId().equals(savedBello.getId().toString())).findFirst().get();
        Assertions.assertEquals(belloSyncStatus.getStatus(), SyncStatus.UPDATED);

        Set<String> idsToSync = Sets.newHashSet(belloSyncStatus.getId());

        securityContext.login(TestPrincipal.withName(KAHN));
        String json = perform(post(petController.getFindSomeUrl())
                .content(getController().getJsonMapper().writeDto(idsToSync))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString();
        RapidSecurityContext.logout();

        CollectionType petSetType = getController().getJsonMapper().getObjectMapper()
                .getTypeFactory().constructCollectionType(Set.class, PetDto.class);
        Set<PetDto> updatedPets = deserialize(json, petSetType);

        Assertions.assertEquals(1,updatedPets.size());

        PetDto belloDto = updatedPets.stream().filter(s -> s.getId().equals(savedBello.getId())).findFirst().get();
        Assertions.assertEquals(belloDto.getBirthDate(),updateBello.getBirthDate());
    }

    // would be overkill to record change in foreignkey column as recorded update for sync
    // just check child set controller for updates with parentId filter
    @Test
    public void checkSyncStatusForOwner_afterUnlinkingPetFromCollection_shouldNOTFindUpdate() throws Exception {
        // create owner linked to pet bello and kitty
        // record client ts
        // unlink owners pet bello via update
        // check owner sync info
        // server tells client, owner was not updated
        // fetch update and validate

        Pet savedBello = petRepository.save(bello);
        Pet savedKitty = petRepository.save(kitty);
        Owner owner = saveOwnerLinkedToPets(kahn,savedBello.getId(),savedKitty.getId());

        assertOwnerHasPets(KAHN,BELLO,KITTY);
        assertPetHasOwner(BELLO,KAHN);
        assertPetHasOwner(KITTY,KAHN);

        Timestamp lastServerUpdate = new Timestamp(owner.getLastModifiedDate().getTime());

        // now
        Timestamp clientUpdate = new Timestamp(new Date().getTime());
        Assertions.assertTrue(clientUpdate.after(lastServerUpdate));

        fetchOwnerSyncStatus_assertNoUpdate(owner.getId(),clientUpdate);

        // update owner by unlinking pet bello
        Owner unlinkBello = Entity.createUpdate(owner);
        Set<Pet> updatedPets = Sets.newHashSet(owner.getPets());
        updatedPets.remove(savedBello);
        Assertions.assertEquals(1,updatedPets.size());
        unlinkBello.setPets(updatedPets);

        Owner updatedOwner = ownerService.partialUpdate(unlinkBello,Sets.newHashSet("pets"));

        assertOwnerHasPets(KAHN,KITTY);
        assertPetHasOwner(BELLO,null);
        assertPetHasOwner(KITTY,KAHN);


        // has not changed
        Assertions.assertEquals(lastServerUpdate, updatedOwner.getLastModifiedDate());


        fetchOwnerSyncStatus_assertNoUpdate(owner.getId(), clientUpdate);

        Set<String> idsToSync = Sets.newHashSet(owner.getId().toString());

        securityContext.login(TestPrincipal.withName(KAHN));
        String json = perform(post(getFindSomeUrl())
                .content(getController().getJsonMapper().writeDto(idsToSync))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString();
        RapidSecurityContext.logout();

        CollectionType ownerSetType = getController().getJsonMapper().getObjectMapper()
                .getTypeFactory().constructCollectionType(Set.class, ReadOwnOwnerDto.class);
        Set<ReadOwnOwnerDto> updatedOwners = deserialize(json, ownerSetType);

        Assertions.assertEquals(1,updatedOwners.size());

        ReadOwnOwnerDto ownerDto = updatedOwners.stream().filter(s -> s.getId().equals(updatedOwner.getId())).findFirst().get();
        Assertions.assertEquals(1,ownerDto.getPetIds().size());
        Assertions.assertEquals(ownerDto.getPetIds().stream().findFirst().get(),savedKitty.getId());
    }

    @Test
    public void checkSyncStatusForOwner_afterUnlinkingAllPetsFromCollection_shouldNOTFindUpdate() throws Exception {
        // create owner linked to pet bello and kitty
        // record client ts
        // unlink all owners pets
        // check owner sync info
        // server tells client, owner was not updated
        // fetch update and validate

        Pet savedBello = petRepository.save(bello);
        Pet savedKitty = petRepository.save(kitty);
        Owner owner = saveOwnerLinkedToPets(kahn,savedBello.getId(),savedKitty.getId());

        assertOwnerHasPets(KAHN,BELLO,KITTY);
        assertPetHasOwner(BELLO,KAHN);
        assertPetHasOwner(KITTY,KAHN);

        Timestamp lastServerUpdate = new Timestamp(owner.getLastModifiedDate().getTime());

        // now
        Timestamp clientUpdate = new Timestamp(new Date().getTime());
        Assertions.assertTrue(clientUpdate.after(lastServerUpdate));

        fetchOwnerSyncStatus_assertNoUpdate(owner.getId(),clientUpdate);

        // update owner by unlinking all pets
        Owner unlinkPets = Entity.createUpdate(owner);
        unlinkPets.setPets(new HashSet<>());

        Owner updatedOwner = ownerService.partialUpdate(unlinkPets,Sets.newHashSet("pets"));

        assertOwnerHasPets(KAHN);
        assertPetHasOwner(BELLO,null);
        assertPetHasOwner(KITTY,null);


        // has not changed
        Assertions.assertEquals(lastServerUpdate, updatedOwner.getLastModifiedDate());

        fetchOwnerSyncStatus_assertNoUpdate(owner.getId(),clientUpdate);
    }


    // collection is annotated with AuditCollection so changes will trigger lastModified change
    // implemented in AuditCollectionsExtension
    @Test
    public void checkSyncStatusForOwner_afterUnlinkingOneHobby_fromAnnotatedAuditCollection_shouldFindUpdate() throws Exception {
        // create owner with hobbies
        // record client ts
        // unlink one hobby from owner
        // check owner sync info
        // server tells client, owner was updated
        // fetch update and validate

        String bodybuilding = "bodybuilding";
        Set<String> hobbies = new HashSet<>(Arrays.asList("swimming","biking",bodybuilding,"jogging","eating"));
        kahn.setHobbies(hobbies);
        Owner owner = saveOwnerLinkedToPets(kahn);

        Assertions.assertEquals(hobbies,owner.getHobbies());

        Timestamp lastServerUpdate = new Timestamp(owner.getLastModifiedDate().getTime());

        // now
        Timestamp clientUpdate = new Timestamp(new Date().getTime());
        Assertions.assertTrue(clientUpdate.after(lastServerUpdate));

        fetchOwnerSyncStatus_assertNoUpdate(owner.getId(),clientUpdate);

        // update owner by unlinking all pets
        Owner removeHobby = Entity.createUpdate(owner);
        Set<String> updatedHobbies = new HashSet<>(hobbies);
        updatedHobbies.remove(bodybuilding);
        removeHobby.setHobbies(updatedHobbies);

        Owner updatedOwner = ownerService.partialUpdate(removeHobby,Sets.newHashSet("hobbies"));


        // has changed
        Assertions.assertTrue(lastServerUpdate.before(updatedOwner.getLastModifiedDate()));

//        EntitySyncStatus status = fetchOwnerSyncStatus_assertUpdate(owner.getId(), clientUpdate, SyncStatus.UPDATED);
        EntitySyncStatus status = fetchOwnerSyncStatus_assertUpdate(owner.getId(), clientUpdate, SyncStatus.UPDATED);

        securityContext.login(TestPrincipal.withName(KAHN));
        ReadOwnOwnerDto readOwnOwnerDto = performDs2xx(find(status.getId()), ReadOwnOwnerDto.class);
        RapidSecurityContext.logout();

        Assertions.assertEquals(updatedHobbies,readOwnOwnerDto.getHobbies());
    }

    // collection is annotated with AuditCollection so changes will trigger lastModified change
    // implemented in AuditCollectionsExtension
    @Test
    public void checkSyncStatusForOwner_after_fromAnnotatedAuditCollection_shouldFindUpdate() throws Exception {
        // create owner with hobbies
        // record client ts
        // unlink one hobby from owner
        // check owner sync info
        // server tells client, owner was updated
        // fetch update and validate

        String bodybuilding = "bodybuilding";
        Set<String> hobbies = new HashSet<>(Arrays.asList("swimming","biking",bodybuilding,"jogging","eating"));
        kahn.setHobbies(hobbies);
        Owner owner = saveOwnerLinkedToPets(kahn);

        Assertions.assertEquals(hobbies,owner.getHobbies());

        Timestamp lastServerUpdate = new Timestamp(owner.getLastModifiedDate().getTime());

        // now
        Timestamp clientUpdate = new Timestamp(new Date().getTime());
        Assertions.assertTrue(clientUpdate.after(lastServerUpdate));

        fetchOwnerSyncStatus_assertNoUpdate(owner.getId(),clientUpdate);

        // update owner by unlinking all pets
        Owner removeHobby = Entity.createUpdate(owner);
        Set<String> updatedHobbies = new HashSet<>(hobbies);
        updatedHobbies.remove(bodybuilding);
        removeHobby.setHobbies(updatedHobbies);

        Owner updatedOwner = ownerService.partialUpdate(removeHobby,Sets.newHashSet("hobbies"));


        // has changed
        Assertions.assertTrue(lastServerUpdate.before(updatedOwner.getLastModifiedDate()));

//        EntitySyncStatus status = fetchOwnerSyncStatus_assertUpdate(owner.getId(), clientUpdate, SyncStatus.UPDATED);
        EntitySyncStatus status = fetchOwnerSyncStatus_assertUpdate(owner.getId(), clientUpdate, SyncStatus.UPDATED);

        securityContext.login(TestPrincipal.withName(KAHN));
        ReadOwnOwnerDto readOwnOwnerDto = performDs2xx(find(status.getId()), ReadOwnOwnerDto.class);
        RapidSecurityContext.logout();

        Assertions.assertEquals(updatedHobbies,readOwnOwnerDto.getHobbies());
    }

    @Test
    public void checkSyncStatusForOwner_afterUnlinkingClinicCard_shouldFindUpdate() throws Exception {
        // create owner linked to single entity clinic card
        // record client ts
        // unlink clinic card from owner
        // check owner sync info
        // server tells client, owner was updated
        // fetch update and validate

        ClinicCard card = clinicCardRepository.save(clinicCard);
        Owner owner = saveOwnerLinkedToClinicCard(kahn,clinicCard);

        assertOwnerHasClinicCard(KAHN,card.getId());
        assertClinicCardHasOwner(card.getId(),KAHN);

        Timestamp lastServerUpdate = new Timestamp(owner.getLastModifiedDate().getTime());

        // now
        Timestamp clientUpdate = new Timestamp(new Date().getTime());
        Assertions.assertTrue(clientUpdate.after(lastServerUpdate));

        // update owner by unlinking pet bello
        Owner unlinkClinicCard = Entity.createUpdate(owner);

        Owner updatedOwner = ownerService.partialUpdate(unlinkClinicCard,"clinicCard");

        assertOwnerHasClinicCard(KAHN, null);
        assertClinicCardHasOwner(card.getId(), null);


        // has changed
        Assertions.assertTrue(lastServerUpdate.before(updatedOwner.getLastModifiedDate()));


        EntitySyncStatus status = fetchOwnerSyncStatus_assertUpdate(owner.getId(), clientUpdate, SyncStatus.UPDATED);

        Set<String> idsToSync = Sets.newHashSet(status.getId());

        securityContext.login(TestPrincipal.withName(KAHN));
        String json = perform(post(getFindSomeUrl())
                .content(getController().getJsonMapper().writeDto(idsToSync))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString();
        RapidSecurityContext.logout();

        CollectionType ownerSetType = getController().getJsonMapper().getObjectMapper()
                .getTypeFactory().constructCollectionType(Set.class, ReadOwnOwnerDto.class);
        Set<ReadOwnOwnerDto> updatedOwners = deserialize(json, ownerSetType);

        Assertions.assertEquals(1,updatedOwners.size());

        ReadOwnOwnerDto ownerDto = updatedOwners.stream().filter(s -> s.getId().equals(updatedOwner.getId())).findFirst().get();
        Assertions.assertNull(ownerDto.getClinicCardId());
    }

    @Test
    public void checkSyncStatusForOwner_afterUnlinkingClinicCard_viaRemovingClinicCard_shouldFindUpdate() throws Exception {
        // create owner linked to single entity clinic card
        // record client ts
        // remove clinicCard -> auto bidir removes card from owner as well
        // check owner sync info
        // server tells client, owner was updated, bc clinic card field and not collection
        // so its updated even though it was indirect
        // fetch update and validate

        ClinicCard card = clinicCardRepository.save(clinicCard);
        Owner owner = saveOwnerLinkedToClinicCard(kahn,clinicCard);

        assertOwnerHasClinicCard(KAHN,card.getId());
        assertClinicCardHasOwner(card.getId(),KAHN);

        Timestamp lastServerUpdate = new Timestamp(owner.getLastModifiedDate().getTime());

        // now
        Timestamp clientUpdate = new Timestamp(new Date().getTime());
        Assertions.assertTrue(clientUpdate.after(lastServerUpdate));

        // update owner by removing clinic card
        clinicCardService.deleteById(card.getId());
        Owner updatedOwner = fetchOwner(owner.getId());

        assertOwnerHasClinicCard(KAHN, null);
        Assertions.assertTrue(clinicCardRepository.findAll().isEmpty());


        // has changed
        Assertions.assertTrue(lastServerUpdate.before(updatedOwner.getLastModifiedDate()));


        EntitySyncStatus status = fetchOwnerSyncStatus_assertUpdate(owner.getId(), clientUpdate, SyncStatus.UPDATED);

        Set<String> idsToSync = Sets.newHashSet(status.getId());

        securityContext.login(TestPrincipal.withName(KAHN));
        String json = perform(post(getFindSomeUrl())
                .content(getController().getJsonMapper().writeDto(idsToSync))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString();
        RapidSecurityContext.logout();

        CollectionType ownerSetType = getController().getJsonMapper().getObjectMapper()
                .getTypeFactory().constructCollectionType(Set.class, ReadOwnOwnerDto.class);
        Set<ReadOwnOwnerDto> updatedOwners = deserialize(json, ownerSetType);

        Assertions.assertEquals(1,updatedOwners.size());

        ReadOwnOwnerDto ownerDto = updatedOwners.stream().filter(s -> s.getId().equals(updatedOwner.getId())).findFirst().get();
        Assertions.assertNull(ownerDto.getClinicCardId());
    }


    // sync specific helpers

    public EntitySyncStatus fetchOwnerSyncStatus_assertUpdate(Long ownerId, Date lastClientUpdate, SyncStatus expectedStatus) throws Exception {

        String responseString = perform(get(ownerSyncController.getFetchEntitySyncStatusUrl())
                .param("id", ownerId.toString())
                .param("ts", String.valueOf(lastClientUpdate.getTime())))
                .andExpect(status().is(200))
                .andReturn().getResponse().getContentAsString();


        EntitySyncStatus status = syncStatusSerializer.deserialize(responseString);
        Assertions.assertEquals(status.getStatus(), expectedStatus);
        Assertions.assertEquals(status.getId(),ownerId.toString());
        return status;
    }

    public void fetchOwnerSyncStatus_assertNoUpdate(Long ownerId, Date lastClientUpdate) throws Exception {
        perform(get(ownerSyncController.getFetchEntitySyncStatusUrl())
                .param("id", ownerId.toString())
                .param("ts", String.valueOf(lastClientUpdate.getTime())))
                .andExpect(status().is(204))
                .andExpect(content().string(""));
    }

    public void fetchOwnerSyncStatusesSinceTs_assertNoUpdates(Date clientUpdate, String... jpqlFilters) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get(ownerSyncController.getFetchEntitySyncStatusesSinceTsUrl())
                .param("ts", String.valueOf(clientUpdate.getTime()));
        if (jpqlFilters.length != 0){
            Assertions.assertEquals(1,jpqlFilters.length);
            requestBuilder.param("jpql-filter",jpqlFilters[0]);
        }
        perform(requestBuilder)
                .andExpect(status().is(204))
                .andExpect(content().string(""));
    }

    public Set<EntitySyncStatus> fetchOwnerSyncStatusesSinceTs_assertUpdates(Timestamp clientUpdate, UrlExtension... jpqlFilters) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get(ownerSyncController.getFetchEntitySyncStatusesSinceTsUrl())
                .param("ts", String.valueOf(clientUpdate.getTime()));
        if (jpqlFilters.length != 0){
            for (UrlExtension filter : jpqlFilters) {
                Assertions.assertTrue(QueryFilter.class.isAssignableFrom(filter.getExtensionType()));
            }
            RapidTestUtil.addUrlExtensionsToRequest(applicationContext,requestBuilder,jpqlFilters);
        }
        String responseString = perform(requestBuilder)
                .andExpect(status().is(200))
                .andReturn().getResponse().getContentAsString();

        return syncStatusSerializer.deserializeToSet(responseString);
    }

    public Set<EntitySyncStatus> fetchPetSyncStatusesSinceTs_assertUpdates(Timestamp clientUpdate, UrlExtension... jpqlFilters) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get(petSyncController.getFetchEntitySyncStatusesSinceTsUrl())
                .param("ts", String.valueOf(clientUpdate.getTime()));
        if (jpqlFilters.length != 0){
            for (UrlExtension filter : jpqlFilters) {
                Assertions.assertTrue(QueryFilter.class.isAssignableFrom(filter.getExtensionType()));
            }
            RapidTestUtil.addUrlExtensionsToRequest(applicationContext,requestBuilder,jpqlFilters);
        }
        String responseString = perform(requestBuilder)
                .andExpect(status().is(200))
                .andReturn().getResponse().getContentAsString();

        return syncStatusSerializer.deserializeToSet(responseString);
    }

    public Set<EntitySyncStatus> fetchOwnerSyncStatuses_assertUpdates(Set<EntityLastUpdateInfo> updateInfos) throws Exception {
        String jsonUpdateInfos = getController().getJsonMapper().writeDto(updateInfos);
        String responseString = perform(post(ownerSyncController.getFetchEntitySyncStatusesUrl())
                .content(jsonUpdateInfos).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andReturn().getResponse().getContentAsString();

        return syncStatusSerializer.deserializeToSet(responseString);
    }

    public void fetchOwnerSyncStatuses_assertNoUpdates(Set<EntityLastUpdateInfo> updateInfos) throws Exception {
        String jsonUpdateInfos = getController().getJsonMapper().writeDto(updateInfos);
        perform(post(ownerSyncController.getFetchEntitySyncStatusesUrl())
                .content(jsonUpdateInfos))
                .andExpect(status().is(204))
                .andExpect(content().string(""));
    }




}