package com.github.vincemann.springrapid.syncdemo.controller;

import com.fasterxml.jackson.databind.type.CollectionType;
import com.github.vincemann.springrapid.core.Entity;
import com.github.vincemann.springrapid.sync.controller.EntitySyncStatusSerializer;
import com.github.vincemann.springrapid.sync.model.EntitySyncStatus;
import com.github.vincemann.springrapid.sync.model.SyncStatus;
import com.github.vincemann.springrapid.syncdemo.controller.sync.OwnerSyncController;
import com.github.vincemann.springrapid.syncdemo.dto.owner.ReadOwnOwnerDto;
import com.github.vincemann.springrapid.syncdemo.model.Owner;
import com.github.vincemann.springrapid.syncdemo.service.OwnerService;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OwnerSyncControllerIntegrationTest extends AbstractControllerIntegrationTest<OwnerController, OwnerService>{

    @Autowired
    OwnerSyncController ownerSyncController;

    @Autowired
    EntitySyncStatusSerializer syncStatusSerializer;


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

        fetchOwnerSyncStatus_assertNeedUpdate(owner.getId(),oneHourBeforeServerUpdate,SyncStatus.UPDATED);
    }

    @Test
    public void checkSyncStatus_whenLastClientUpdateAfterEntityUpdate() throws Exception {
        // create owner
        // record client ts as now + 1 hour -> far after creation of owner
        // check if owner was updated -> no
        Owner owner = fetchOwner(saveOwnerLinkedToPets(kahn).getId());
        Assertions.assertNotNull(owner.getCreatedDate());
        Assertions.assertNotNull(owner.getCreatedById());
        Assertions.assertNotNull(owner.getLastModifiedDate());
        Assertions.assertNotNull(owner.getLastModifiedById());

        Timestamp lastServerUpdate = new Timestamp(owner.getLastModifiedDate().getTime());

        // Subtract one hour (3600,000 milliseconds) from the current timestamp
        long oneHourInMillis = 3600_000;
        long newTimestampInMillis = lastServerUpdate.getTime() + oneHourInMillis;

        // Create a new Timestamp from the adjusted milliseconds value
        Timestamp oneHourAfterServerUpdate = new Timestamp(newTimestampInMillis);

        Assertions.assertTrue(oneHourAfterServerUpdate.after(lastServerUpdate));

        fetchOwnerSyncStatus_assertNoUpdateNeeded(owner.getId(),oneHourAfterServerUpdate);
    }

    @Test
    public void lastUpdateNow_checkSyncStatus_noUpdateRequired() throws Exception {
        // create owner
        // record client ts as now
        // check if owner was updated -> no
        Owner owner = fetchOwner(saveOwnerLinkedToPets(kahn).getId());
        Assertions.assertNotNull(owner.getCreatedDate());
        Assertions.assertNotNull(owner.getCreatedById());
        Assertions.assertNotNull(owner.getLastModifiedDate());
        Assertions.assertNotNull(owner.getLastModifiedById());

        Timestamp lastServerUpdate = new Timestamp(owner.getLastModifiedDate().getTime());

        // now
        Timestamp now = new Timestamp(new Date().getTime());

        Assertions.assertTrue(now.after(lastServerUpdate));

        fetchOwnerSyncStatus_assertNoUpdateNeeded(owner.getId(),now);
    }

    @Test
    public void checkSyncStatus_noSyncNeeded_updateEntity_checkSyncStatus_updateRequired_fetchUpdate() throws Exception {
        // create owner
        // record client ts as now
        // check if owner was updated -> no
        // update owner's number
        // check if owner was updated -> yes
        // fetch owner with id from sync info and validate number changed

        Owner owner = saveOwnerLinkedToPets(kahn);

        Timestamp lastServerUpdate = new Timestamp(owner.getLastModifiedDate().getTime());

        // now
        Timestamp clientUpdate = new Timestamp(new Date().getTime());
        Assertions.assertTrue(clientUpdate.after(lastServerUpdate));


        fetchOwnerSyncStatus_assertNoUpdateNeeded(owner.getId(),clientUpdate);


        String updatedLastName = owner.getLastName()+"-updated";
        Owner updateOwner = Entity.createUpdate(owner);
        updateOwner.setLastName(updatedLastName);

        Owner updated = ownerService.partialUpdate(updateOwner);
        Assertions.assertEquals(updatedLastName,updated.getLastName());
        Assertions.assertTrue(updated.getLastModifiedDate().after(lastServerUpdate));
        Assertions.assertTrue(clientUpdate.before(updated.getLastModifiedDate()));

        // now should need update
        EntitySyncStatus status = fetchOwnerSyncStatus_assertNeedUpdate(owner.getId(), clientUpdate, SyncStatus.UPDATED);

        ReadOwnOwnerDto updatedEG = performDs2xx(testTemplate.find(status.getId())
                , ReadOwnOwnerDto.class);

        Assertions.assertEquals(updatedLastName,updatedEG.getLastName());
    }


    @Test
    public void checkSyncStatusForAllEgs_sinceTimestamp() throws Exception {
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

        fetchOwnerSyncStatusesSinceTs_assertNone(clientUpdate);
        // update owner 2 and 3

        Owner updateOwner2 = Entity.createUpdate(owner2);
        updateOwner2.setLastName("new 2 lastname");


        Owner updateOwner3 = Entity.createUpdate(owner3);
        updateOwner3.setLastName("new 3 lastname");

        Owner updatedOwner2 = ownerService.partialUpdate(updateOwner2);
        Owner updatedOwner3 = ownerService.partialUpdate(updateOwner3);

        Assertions.assertTrue(updatedOwner2.getLastModifiedDate().after(lastServerUpdate));
        Assertions.assertTrue(updatedOwner3.getLastModifiedDate().after(lastServerUpdate));

        // now should need update for owner2 and owner3
        Set<EntitySyncStatus> statuses = fetchOwnerSyncStatusesSinceTs_assertUpdatesNeeded(clientUpdate);
        Assertions.assertEquals(2,statuses.size());
        EntitySyncStatus owner2SyncStatus = statuses.stream().filter(s -> s.getId().equals(updatedOwner2.getId().toString())).findFirst().get();
        EntitySyncStatus owner3SyncStatus = statuses.stream().filter(s -> s.getId().equals(updatedOwner3.getId().toString())).findFirst().get();


        Assertions.assertEquals(owner2SyncStatus.getStatus(), SyncStatus.UPDATED);
        Assertions.assertEquals(owner3SyncStatus.getStatus(), SyncStatus.UPDATED);

        Set<String> idsToSync = Sets.newHashSet(owner2SyncStatus.getId(),owner3SyncStatus.getId());

        String json = perform(post(getFindSomeUrl())
                .content(getController().getJsonMapper().writeDto(idsToSync))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString();

        CollectionType ownerSetType = getController().getJsonMapper().getObjectMapper()
                .getTypeFactory().constructCollectionType(Set.class, ReadOwnOwnerDto.class);
        Set<ReadOwnOwnerDto> updatedOwners = deserialize(json, ownerSetType);

        Assertions.assertEquals(2,updatedOwners.size());

        ReadOwnOwnerDto owner2Dto = updatedOwners.stream().filter(s -> s.getId().equals(updatedOwner2.getId())).findFirst().get();
        ReadOwnOwnerDto owner3Dto = updatedOwners.stream().filter(s -> s.getId().equals(updatedOwner3.getId())).findFirst().get();

        Assertions.assertEquals(owner2Dto.getLastName(),updateOwner2.getLastName());
        Assertions.assertEquals(owner3Dto.getLastName(),updateOwner3.getLastName());
    }


    // sync specific helpers

    public EntitySyncStatus fetchOwnerSyncStatus_assertNeedUpdate(Long ownerId, Date lastClientUpdate, SyncStatus expectedStatus) throws Exception {

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

    public void fetchOwnerSyncStatus_assertNoUpdateNeeded(Long ownerId, Date lastClientUpdate) throws Exception {
        perform(get(ownerSyncController.getFetchEntitySyncStatusUrl())
                .param("id", ownerId.toString())
                .param("ts", String.valueOf(lastClientUpdate.getTime())))
                .andExpect(status().is(204))
                .andExpect(content().string(""));
    }

    public void fetchOwnerSyncStatusesSinceTs_assertNone(Date clientUpdate) throws Exception {
        perform(get(ownerSyncController.getFetchEntitySyncStatusesSinceTsUrl())
                .param("ts", String.valueOf(clientUpdate.getTime())))
                .andExpect(status().is(204))
                .andExpect(content().string(""));
    }

    public Set<EntitySyncStatus> fetchOwnerSyncStatusesSinceTs_assertUpdatesNeeded(Date clientUpdate) throws Exception {
        String responseString = perform(get(ownerSyncController.getFetchEntitySyncStatusesSinceTsUrl())
                .param("ts", String.valueOf(clientUpdate.getTime())))
                .andExpect(status().is(200))
                .andReturn().getResponse().getContentAsString();

        return syncStatusSerializer.deserializeToSet(responseString);
    }




}
