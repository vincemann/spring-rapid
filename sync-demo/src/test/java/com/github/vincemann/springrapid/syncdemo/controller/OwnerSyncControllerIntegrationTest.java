package com.github.vincemann.springrapid.syncdemo.controller;

import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;
import com.github.vincemann.springrapid.coretest.util.TestPrincipal;
import com.github.vincemann.springrapid.sync.model.EntitySyncStatus;
import com.github.vincemann.springrapid.sync.model.LastFetchInfo;
import com.github.vincemann.springrapid.sync.model.SyncStatus;
import com.github.vincemann.springrapid.syncdemo.controller.suite.MyIntegrationTest;
import com.github.vincemann.springrapid.syncdemo.controller.suite.template.OwnerSyncControllerTestTemplate;
import com.github.vincemann.springrapid.syncdemo.controller.suite.template.PetControllerTestTemplate;
import com.github.vincemann.springrapid.syncdemo.controller.suite.template.PetSyncControllerTestTemplate;
import com.github.vincemann.springrapid.syncdemo.dto.owner.ReadOwnerDto;
import com.github.vincemann.springrapid.syncdemo.dto.pet.ReadPetDto;
import com.github.vincemann.springrapid.syncdemo.model.ClinicCard;
import com.github.vincemann.springrapid.syncdemo.model.Owner;
import com.github.vincemann.springrapid.syncdemo.model.Pet;
import com.github.vincemann.springrapid.syncdemo.model.Toy;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.Nullable;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import java.sql.Timestamp;
import java.util.*;

@Tag(value = "demo-projects")
public class OwnerSyncControllerIntegrationTest extends MyIntegrationTest {


    @Autowired
    OwnerSyncControllerTestTemplate ownerSyncController;

    @Autowired
    PetControllerTestTemplate petController;

    @Autowired
    PetSyncControllerTestTemplate petSyncController;


    @Autowired
    ApplicationContext applicationContext;


    @Test
    public void givenClientTimestampWasRecordedBeforeCreating_whenFindSyncStatus_thenMarkedAsUpdated() throws Exception {
        // create owner
        // record client ts as now - 1 hour -> far before creation of owner
        // check if owner was updated -> yes
        Owner owner = createOwnerLinkedToPets(kahn);

        Timestamp lastServerUpdate = new Timestamp(owner.getLastModifiedDate().getTime());

        // Subtract one hour (3600,000 milliseconds) from the current timestamp
        long oneHourInMillis = 3600_000;
        long newTimestampInMillis = lastServerUpdate.getTime() - oneHourInMillis;

        // Create a new Timestamp from the adjusted milliseconds value
        Timestamp oneHourBeforeServerUpdate = new Timestamp(newTimestampInMillis);

        Assertions.assertTrue(oneHourBeforeServerUpdate.before(lastServerUpdate));

        ownerSyncController.fetchSyncStatus_assertUpdate(owner.getId(),oneHourBeforeServerUpdate,SyncStatus.UPDATED);
    }

    @Test
    public void givenClientTimestampWasRecordedAfterCreating_whenFindSyncStatus_thenMarkedAsUpdated() throws Exception {
        // create owner
        // record client ts as now + 1 hour -> far after creation of owner
        // check if owner was updated -> no
        Owner owner = fetchOwner(createOwnerLinkedToPets(kahn).getId());

        Timestamp lastServerUpdate = new Timestamp(owner.getLastModifiedDate().getTime());

        long oneHourInMillis = 3600_000;
        long newTimestampInMillis = lastServerUpdate.getTime() + oneHourInMillis;

        // Create a new Timestamp from the adjusted milliseconds value
        Timestamp oneHourAfterServerUpdate = new Timestamp(newTimestampInMillis);

        Assertions.assertTrue(oneHourAfterServerUpdate.after(lastServerUpdate));

        ownerSyncController.fetchSyncStatus_assertNoUpdate(owner.getId(),oneHourAfterServerUpdate);
    }


    @Test
    public void givenEntityWasDeleted_whenFindSyncStatus_thenMarkedAsRemoved() throws Exception {
        // create owner
        // record client ts as now
        // check if owner was updated -> no
        // remove owner
        // check for owner update and get UpdateInfo (removed)
        Owner owner = fetchOwner(createOwnerLinkedToPets(kahn).getId());

        Timestamp lastServerUpdate = new Timestamp(owner.getLastModifiedDate().getTime());

        // now
        Timestamp now = new Timestamp(new Date().getTime());

        Assertions.assertTrue(now.after(lastServerUpdate));

        ownerSyncController.fetchSyncStatus_assertNoUpdate(owner.getId(),now);

        ownerRepository.deleteById(owner.getId());

        Assertions.assertFalse(ownerRepository.findById(owner.getId()).isPresent());

        ownerSyncController.fetchSyncStatus_assertUpdate(owner.getId(),now,SyncStatus.REMOVED);
    }

    @Test
    public void givenOwnersNameUpdate_whenFindSyncStatus_thenMarkedAsUpdated() throws Exception {
        // create owner
        // record client ts as now
        // check if owner was updated -> no
        // update owner's first name
        // check if owner was updated -> yes
        // fetch owner with id from sync info and validate first name changed

        Owner owner = createOwnerLinkedToPets(kahn);

        Timestamp lastServerUpdate = new Timestamp(owner.getLastModifiedDate().getTime());

        // now
        Timestamp clientUpdate = new Timestamp(new Date().getTime());
        Assertions.assertTrue(clientUpdate.after(lastServerUpdate));


        ownerSyncController.fetchSyncStatus_assertNoUpdate(owner.getId(),clientUpdate);

        // update first name
        String updatedFirstName = owner.getFirstName()+"-updated";
        Owner updated = transactionTemplate.execute(transactionStatus -> {
            Owner update = ownerRepository.findById(owner.getId()).get();
            update.setFirstName(updatedFirstName);
            return update;
        });


        Assertions.assertEquals(updatedFirstName,updated.getFirstName());
        Assertions.assertTrue(updated.getLastModifiedDate().after(lastServerUpdate));
        Assertions.assertTrue(clientUpdate.before(updated.getLastModifiedDate()));

        // now should need update
        EntitySyncStatus status = ownerSyncController.fetchSyncStatus_assertUpdate(owner.getId(), clientUpdate, SyncStatus.UPDATED);

        RapidSecurityContext.setAuthenticated(TestPrincipal.create(KAHN));
        ReadOwnerDto updatedEG = ownerController.find2xx(Long.valueOf(status.getId()));
        RapidSecurityContext.clear();

        Assertions.assertEquals(updatedFirstName,updatedEG.getFirstName());
    }


    @Test
    public void givenSomeEntitiesUpdated_whenFindAllUpdatesSinceTs_thenUpdatedEntitiesMarkedAsUpdated() throws Exception {
        // create 3 owners
        // record client ts
        // modify 2
        // ask server for updateInfos of all owner's
        // server should tell client about the two updates of owner2 and owner3
        // query server for those updates and validate
        Owner owner = createOwnerLinkedToPets(kahn);
        Owner owner2 = createOwnerLinkedToPets(meier);
        Owner owner3 = createOwnerLinkedToPets(gil);

        Timestamp lastServerUpdate = new Timestamp(owner.getLastModifiedDate().getTime());

        // now
        Timestamp clientUpdate = new Timestamp(new Date().getTime());
        Assertions.assertTrue(clientUpdate.after(lastServerUpdate));

        ownerSyncController.fetchSyncStatusesSinceTs_assertNoUpdates(clientUpdate);

        // update owner 2 and 3
        String owner2City = owner2.getCity() + "updated";
        Owner updatedOwner2 = transactionTemplate.execute(status -> {
            Owner update2 = ownerRepository.findById(owner2.getId()).get();
            update2.setCity(owner2City);
            return update2;
        });

        String owner3City = owner3.getCity() + "updated";
        Owner updatedOwner3 = transactionTemplate.execute(status -> {
            Owner update3 = ownerRepository.findById(owner3.getId()).get();
            update3.setCity(owner3City);
            return update3;
        });

        Assertions.assertTrue(updatedOwner2.getLastModifiedDate().after(lastServerUpdate));
        Assertions.assertTrue(updatedOwner3.getLastModifiedDate().after(lastServerUpdate));

        // now should need update for owner2 and owner3
        List<EntitySyncStatus> statuses = ownerSyncController.fetchSyncStatusesSinceTs_assertUpdates(clientUpdate);
        Assertions.assertEquals(2,statuses.size());
        EntitySyncStatus owner2SyncStatus = statuses.stream().filter(s -> s.getId().equals(updatedOwner2.getId().toString())).findFirst().get();
        EntitySyncStatus owner3SyncStatus = statuses.stream().filter(s -> s.getId().equals(updatedOwner3.getId().toString())).findFirst().get();


        Assertions.assertEquals(owner2SyncStatus.getStatus(), SyncStatus.UPDATED);
        Assertions.assertEquals(owner3SyncStatus.getStatus(), SyncStatus.UPDATED);

        List<Long> idsToSync = Lists.newArrayList(Long.valueOf(owner2SyncStatus.getId()),Long.valueOf(owner3SyncStatus.getId()));

        RapidSecurityContext.setAuthenticated(TestPrincipal.create(KAHN));
        List<ReadOwnerDto> updatedOwners= ownerController.findSome2xx(idsToSync);
        RapidSecurityContext.clear();


        Assertions.assertEquals(2,updatedOwners.size());

        ReadOwnerDto owner2Dto = updatedOwners.stream().filter(s -> s.getId().equals(updatedOwner2.getId())).findFirst().get();
        ReadOwnerDto owner3Dto = updatedOwners.stream().filter(s -> s.getId().equals(updatedOwner3.getId())).findFirst().get();

        Assertions.assertEquals(owner2Dto.getCity(),owner2City);
        Assertions.assertEquals(owner3Dto.getCity(),owner3City);
    }

    // cant find removed entities
    @Test
    public void givenSomeEntitiesUpdatedAndOneRemoved_whenFindAllUpdatesSinceTs_thenOnlyUpdatedEntitiesMarkedAsUpdatedAndRemovedNotReturned() throws Exception {
        // create 3 owners kahn, meier & gil
        // record client ts
        // modify kahn and remove gil
        // ask server for updateInfos of all owner's
        // server should tell client about update of kahn - cant tell about removal of gil
        // query server for those updates and validate
        Owner savedKahn = createOwnerLinkedToPets(kahn);
        Owner savedMeier = createOwnerLinkedToPets(meier);
        Owner savedGil = createOwnerLinkedToPets(gil);

        Timestamp kahnServerUpdate = new Timestamp(savedKahn.getLastModifiedDate().getTime());
        Timestamp meierServerUpdate = new Timestamp(savedMeier.getLastModifiedDate().getTime());

        // now
        Timestamp clientUpdate = new Timestamp(new Date().getTime());
        Assertions.assertTrue(clientUpdate.after(kahnServerUpdate));
        Assertions.assertTrue(clientUpdate.after(meierServerUpdate));

        ownerSyncController.fetchSyncStatusesSinceTs_assertNoUpdates(clientUpdate);

        // update kahn and remove gil
        String updatedCity = kahn.getCity() + "updated";
        Owner updatedKahn = transactionTemplate.execute(status -> {
            Owner update = ownerRepository.findById(kahn.getId()).get();
            update.setCity(updatedCity);
            ownerRepository.deleteById(savedGil.getId());
            return update;
        });

        Assertions.assertTrue(updatedKahn.getLastModifiedDate().after(kahnServerUpdate));
        Assertions.assertFalse(savedMeier.getLastModifiedDate().after(meierServerUpdate));
        Assertions.assertFalse(ownerRepository.findById(savedGil.getId()).isPresent());

        // now should need update for owner2 and owner3
        List<EntitySyncStatus> statuses = ownerSyncController.fetchSyncStatusesSinceTs_assertUpdates(clientUpdate);
        Assertions.assertEquals(1,statuses.size());
        EntitySyncStatus kahnSyncStatus = statuses.stream().filter(s -> s.getId().equals(updatedKahn.getId().toString())).findFirst().get();

        Assertions.assertEquals(kahnSyncStatus.getStatus(), SyncStatus.UPDATED);

        List<Long> idsToSync = Lists.newArrayList(Long.valueOf(kahnSyncStatus.getId()));

        RapidSecurityContext.setAuthenticated(TestPrincipal.create(KAHN));
        List<ReadOwnerDto> updatedOwners= ownerController.findSome2xx(idsToSync);
        RapidSecurityContext.clear();

        Assertions.assertEquals(1,updatedOwners.size());

        ReadOwnerDto kahnDto = updatedOwners.stream().filter(s -> s.getId().equals(updatedKahn.getId())).findFirst().get();

        Assertions.assertEquals(kahnDto.getCity(),updatedCity);
    }




    @Test
    public void givenOneOfTwoOwnersUpdated_whenFindSyncStatusOfThoseTwo_thenUpdatedOwnerMarkedAsUpdated() throws Exception {
        // create 3 owners
        // record client ts
        // modify owner2 and owner3
        // ask server for sync info of owner2 and owner1
        // server should tell client about update of owner2
        // query server for those updates and validate
        Owner owner = createOwnerLinkedToPets(kahn);
        Owner owner2 = createOwnerLinkedToPets(meier);
        Owner owner3 = createOwnerLinkedToPets(gil);

        Timestamp lastServerUpdate = new Timestamp(owner.getLastModifiedDate().getTime());

        // now
        Timestamp clientUpdate = new Timestamp(new Date().getTime());
        Assertions.assertTrue(clientUpdate.after(lastServerUpdate));

        ownerSyncController.fetchSyncStatusesSinceTs_assertNoUpdates(clientUpdate);

        String owner2City = owner2.getCity() + "updated";
        // update owner 2 and 3
        Owner updatedOwner2 = transactionTemplate.execute(status -> {
            Owner update2 = ownerRepository.findById(owner2.getId()).get();
            update2.setCity(owner2City);
            return update2;
        });

        String owner3City = owner3.getCity() + "updated";
        Owner updatedOwner3 = transactionTemplate.execute(status -> {
            Owner update3 = ownerRepository.findById(owner3.getId()).get();
            update3.setCity(owner3City);
            return update3;
        });

        Assertions.assertTrue(updatedOwner2.getLastModifiedDate().after(lastServerUpdate));
        Assertions.assertTrue(updatedOwner3.getLastModifiedDate().after(lastServerUpdate));

        // now should ask for update info for owner2 and owner3
        List<LastFetchInfo> lastFetchInfos = new ArrayList<>();
        lastFetchInfos.add(new LastFetchInfo(owner.getId(),clientUpdate));
        lastFetchInfos.add(new LastFetchInfo(owner2.getId(),clientUpdate));
        List<EntitySyncStatus> statuses = ownerSyncController.fetchSyncStatuses_assertUpdates(lastFetchInfos);
        Assertions.assertEquals(1,statuses.size());
        EntitySyncStatus owner2SyncStatus = statuses.stream().filter(s -> s.getId().equals(updatedOwner2.getId().toString())).findFirst().get();

        Assertions.assertEquals(owner2SyncStatus.getStatus(), SyncStatus.UPDATED);

        List<Long> idsToSync = Lists.newArrayList(Long.valueOf(owner2SyncStatus.getId()));

        RapidSecurityContext.setAuthenticated(TestPrincipal.create(KAHN));
        List<ReadOwnerDto> updatedOwners= ownerController.findSome2xx(idsToSync);
        RapidSecurityContext.clear();

        Assertions.assertEquals(1,updatedOwners.size());

        ReadOwnerDto owner2Dto = updatedOwners.stream().filter(s -> s.getId().equals(updatedOwner2.getId())).findFirst().get();

        Assertions.assertEquals(owner2Dto.getCity(),owner2City);
    }

    @Test
    public void givenOneOfThreeOwnersUpdatedAndOneRemoved_whenFindSyncStatusOfThoseThree_thenUpdatedOwnerMarkedAsUpdatedAndRemovedMarkedAsRemoved() throws Exception {
        // create 3 owners kahn, meier & gil
        // record client ts
        // modify kahn & remove gil
        // ask server for sync info of kahn,gil,meier
        // server should tell client about update of kahn and removal of gil

        Owner kahn = createOwnerLinkedToPets(this.kahn);
        Owner meier = createOwnerLinkedToPets(this.meier);
        Owner gil = createOwnerLinkedToPets(this.gil);

        // now
        Timestamp clientUpdate = new Timestamp(new Date().getTime());

        ownerSyncController.fetchSyncStatusesSinceTs_assertNoUpdates(clientUpdate);

        // update kahn
        String updatedCity = kahn.getCity() + "updated";
        Owner updatedKahn = transactionTemplate.execute(status -> {
            Owner update = ownerRepository.findById(kahn.getId()).get();
            update.setCity(updatedCity);
            return update;
        });

        ownerRepository.deleteById(gil.getId());

        Assertions.assertTrue(updatedKahn.getLastModifiedDate().after(clientUpdate));
        Assertions.assertTrue(ownerRepository.findById(gil.getId()).isEmpty());


        // now should ask for update info for all owners
        List<LastFetchInfo> lastUpdateInfos = new ArrayList<>();
        lastUpdateInfos.add(new LastFetchInfo(kahn.getId(),clientUpdate));
        lastUpdateInfos.add(new LastFetchInfo(meier.getId(),clientUpdate));
        lastUpdateInfos.add(new LastFetchInfo(gil.getId(),clientUpdate));
        List<EntitySyncStatus> statuses = ownerSyncController.fetchSyncStatuses_assertUpdates(lastUpdateInfos);

        Assertions.assertEquals(2,statuses.size());
        EntitySyncStatus kahnSyncStatus = statuses.stream().filter(s -> s.getId().equals(updatedKahn.getId().toString())).findFirst().get();
        EntitySyncStatus gilSyncStatus = statuses.stream().filter(s -> s.getId().equals(gil.getId().toString())).findFirst().get();

        Assertions.assertEquals(kahnSyncStatus.getStatus(), SyncStatus.UPDATED);
        Assertions.assertEquals(gilSyncStatus.getStatus(), SyncStatus.REMOVED);
    }

    @Test
    public void givenTwoOwnersOfThreeUpdated_whenFindSyncStatusesOfThoseThreeWithDifferentLastClientUpdateTimestamps_thenTakeDiffTimestampsIntoAccount() throws Exception {
        // create 3 owners
        // record client ts1
        // modify owner2 and owner3
        // record client ts2
        // ask server for sync info of owner2:ts1 and owner3:ts2
        // server should tell client about update of owner2 (owner3 ts is after last update -> no update required)
        // query server for those updates and validate
        Owner owner = createOwnerLinkedToPets(kahn);
        Owner owner2 = createOwnerLinkedToPets(meier);
        Owner owner3 = createOwnerLinkedToPets(gil);

        Timestamp lastServerUpdate = new Timestamp(owner.getLastModifiedDate().getTime());

        // now
        Timestamp clientUpdate1 = new Timestamp(new Date().getTime());
        Assertions.assertTrue(clientUpdate1.after(lastServerUpdate));

        ownerSyncController.fetchSyncStatusesSinceTs_assertNoUpdates(clientUpdate1);

        // update owner 2 and 3
        String owner2City = owner2.getCity() + "updated";
        Owner updatedOwner2 = transactionTemplate.execute(status -> {
            Owner update2 = ownerRepository.findById(owner2.getId()).get();
            update2.setCity(owner2City);
            return update2;
        });

        String owner3City = owner3.getCity() + "updated";
        Owner updatedOwner3 = transactionTemplate.execute(status -> {
            Owner update3 = ownerRepository.findById(owner3.getId()).get();
            update3.setCity(owner3City);
            return update3;
        });

        Timestamp clientUpdate2 = new Timestamp(new Date().getTime());

        Assertions.assertTrue(updatedOwner2.getLastModifiedDate().after(lastServerUpdate));
        Assertions.assertTrue(updatedOwner3.getLastModifiedDate().after(lastServerUpdate));
        Assertions.assertTrue(updatedOwner2.getLastModifiedDate().before(clientUpdate2));
        Assertions.assertTrue(updatedOwner3.getLastModifiedDate().before(clientUpdate2));

        // now should ask for update info for owner2 and owner3
        List<LastFetchInfo> lastUpdateInfos = new ArrayList<>();
        lastUpdateInfos.add(new LastFetchInfo(owner2.getId(),clientUpdate1));
        lastUpdateInfos.add(new LastFetchInfo(owner3.getId(),clientUpdate2));
        List<EntitySyncStatus> statuses = ownerSyncController.fetchSyncStatuses_assertUpdates(lastUpdateInfos);
        Assertions.assertEquals(1,statuses.size());
        EntitySyncStatus owner2SyncStatus = statuses.stream().filter(s -> s.getId().equals(updatedOwner2.getId().toString())).findFirst().get();

        Assertions.assertEquals(owner2SyncStatus.getStatus(), SyncStatus.UPDATED);

        List<Long> idsToSync = Lists.newArrayList(Long.valueOf(owner2SyncStatus.getId()));

        RapidSecurityContext.setAuthenticated(TestPrincipal.create(KAHN));
        List<ReadOwnerDto> updatedOwners= ownerController.findSome2xx(idsToSync);
        RapidSecurityContext.clear();

        Assertions.assertEquals(1,updatedOwners.size());

        ReadOwnerDto owner2Dto = updatedOwners.stream().filter(s -> s.getId().equals(updatedOwner2.getId())).findFirst().get();

        Assertions.assertEquals(owner2Dto.getCity(),owner2City);
    }

    @Test
    public void givenSomeOwnersUpdated_whenCheckSyncStatusForFilteredOwnersSinceTimestamp_thenReturnFilteredUpdatedStatusesOfOwners() throws Exception {
        // create 3 owners
        // record client ts
        // modify owner2 and owner3
        // ask server for sync infos of all owners, that match filter OwnerTelNumberFilter - that have telnr that starts with 0176 -> owner3 and owner1
        // server should tell client about update of owner3 (owner2 was also updated, but did not match filter)
        // query server for those updates and validate
        Owner owner = createOwnerLinkedToPets(kahn);
        Owner owner2 = createOwnerLinkedToPets(meier);
        Owner owner3 = createOwnerLinkedToPets(gil);

        Timestamp lastServerUpdate = new Timestamp(owner.getLastModifiedDate().getTime());

        // now
        Timestamp clientUpdate = new Timestamp(new Date().getTime());
        Assertions.assertTrue(clientUpdate.after(lastServerUpdate));

        ownerSyncController.fetchSyncStatusesSinceTs_assertNoUpdates(clientUpdate);

        // update owner 2 and 3
        String owner2City = owner2.getCity() + "updated";
        Owner updatedOwner2 = transactionTemplate.execute(status -> {
            Owner update2 = ownerRepository.findById(owner2.getId()).get();
            update2.setCity(owner2City);
            return update2;
        });

        String owner3City = owner3.getCity() + "updated";
        Owner updatedOwner3 = transactionTemplate.execute(status -> {
            Owner update3 = ownerRepository.findById(owner3.getId()).get();
            update3.setCity(owner3City);
            return update3;
        });

        Assertions.assertTrue(updatedOwner2.getLastModifiedDate().after(lastServerUpdate));
        Assertions.assertTrue(updatedOwner3.getLastModifiedDate().after(lastServerUpdate));

        // now should ask for update info for owner2 and owner3
        List<EntitySyncStatus> statuses = ownerSyncController.fetchSyncStatusesSinceTsOfOwnersWithTelnrPrefix_assertUpdates(
                clientUpdate,"0176");
        Assertions.assertEquals(1,statuses.size());
        EntitySyncStatus owner3SyncStatus = statuses.stream().filter(s -> s.getId().equals(updatedOwner3.getId().toString())).findFirst().get();

        Assertions.assertEquals(owner3SyncStatus.getStatus(), SyncStatus.UPDATED);

        List<Long> idsToSync = Lists.newArrayList(Long.valueOf(owner3SyncStatus.getId()));

        RapidSecurityContext.setAuthenticated(TestPrincipal.create(KAHN));
        List<ReadOwnerDto> updatedOwners= ownerController.findSome2xx(idsToSync);
        RapidSecurityContext.clear();

        Assertions.assertEquals(1,updatedOwners.size());

        ReadOwnerDto owner3Dto = updatedOwners.stream().filter(s -> s.getId().equals(updatedOwner3.getId())).findFirst().get();
        Assertions.assertEquals(owner3Dto.getCity(),owner3City);
    }


    @Test
    public void givenTwoOwnersUpdatedAndOneRemoved_whenCheckSyncStatusForFilteredOwnersSinceTimestamp_thenReturnOnlyFilteredUpdatedAndNotRemoved() throws Exception {
        // create 3 owners kahn, meier & gil
        // record client ts
        // modify meier and kahn & remove gil
        // ask server for sync infos of all owners, that match filter OwnerTelNumberFilter - that have telnr that starts with 0176 -> gil and kahn
        // server should tell client about update of kahn but cant present info for deleted gil

        Owner kahn = createOwnerLinkedToPets(this.kahn);
        Owner meier = createOwnerLinkedToPets(this.meier);
        Owner gil = createOwnerLinkedToPets(this.gil);

        // now
        Timestamp clientUpdate = new Timestamp(new Date().getTime());

        ownerSyncController.fetchSyncStatusesSinceTs_assertNoUpdates(clientUpdate);

        // update meier and kahn and delete gil
        String meierCity = meier.getCity() + "updated";
        Owner updatedMeier = transactionTemplate.execute(status -> {
            Owner update2 = ownerRepository.findById(meier.getId()).get();
            update2.setCity(meierCity);
            return update2;
        });

        String kahnCity = kahn.getCity() + "updated";
        Owner updatedKahn = transactionTemplate.execute(status -> {
            Owner update3 = ownerRepository.findById(kahn.getId()).get();
            update3.setCity(kahnCity);
            return update3;
        });

        ownerRepository.deleteById(gil.getId());

        Assertions.assertTrue(updatedMeier.getLastModifiedDate().after(clientUpdate));
        Assertions.assertTrue(updatedKahn.getLastModifiedDate().after(clientUpdate));

        // now should ask for update info for owners with tel nr filter
        List<EntitySyncStatus> statuses = ownerSyncController.fetchSyncStatusesSinceTsOfOwnersWithTelnrPrefix_assertUpdates(clientUpdate,"0176");

        Assertions.assertEquals(1,statuses.size());
        EntitySyncStatus kahnSyncStatus = statuses.stream().filter(s -> s.getId().equals(updatedKahn.getId().toString())).findFirst().get();

        Assertions.assertEquals(kahnSyncStatus.getStatus(), SyncStatus.UPDATED);

        List<Long> idsToSync = Lists.newArrayList(Long.valueOf(kahnSyncStatus.getId()));

        RapidSecurityContext.setAuthenticated(TestPrincipal.create(KAHN));
        List<ReadOwnerDto> updatedOwners= ownerController.findSome2xx(idsToSync);
        RapidSecurityContext.clear();

        Assertions.assertEquals(1,updatedOwners.size());

        ReadOwnerDto kahnDto = updatedOwners.stream().filter(s -> s.getId().equals(updatedKahn.getId())).findFirst().get();
        Assertions.assertEquals(kahnDto.getCity(),kahnCity);
    }



    @Test
    public void givenPetsOutsideAndInsideOfFilterUpdated_whenFindUpdatesSinceTsOfPetsWithFilter_thenOnlyReturnUpdatedPetsWithinFilter() throws Exception {
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
        Owner owner = createOwnerLinkedToPets(kahn,savedBello.getId(),savedKitty.getId());
        Owner owner2 = createOwnerLinkedToPets(meier,savedBella.getId());

        Timestamp lastServerUpdate = new Timestamp(owner.getLastModifiedDate().getTime());

        // now
        Timestamp clientUpdate = new Timestamp(new Date().getTime());
        Assertions.assertTrue(clientUpdate.after(lastServerUpdate));

        ownerSyncController.fetchSyncStatusesSinceTs_assertNoUpdates(clientUpdate);

        // update bello and bella
        Pet updatedBello = transactionTemplate.execute(status -> {
            Pet update = petRepository.findById(bello.getId()).get();
            update.setBirthDate(savedBello.getBirthDate().minusDays(3));
            return update;
        });

        Pet updatedBella = transactionTemplate.execute(status -> {
            Pet update = petRepository.findById(bella.getId()).get();
            update.setBirthDate(savedBella.getBirthDate().minusDays(2));
            return update;
        });


        Assertions.assertTrue(updatedBello.getLastModifiedDate().after(lastServerUpdate));
        Assertions.assertTrue(updatedBella.getLastModifiedDate().after(lastServerUpdate));

        // now should ask for update infos for all pets with owner=kahn since clientUpdate ts
        List<EntitySyncStatus> statuses = petSyncController.fetchSyncStatusesSinceTsOfOwner_assertUpdates(clientUpdate,owner.getId());

        Assertions.assertEquals(1,statuses.size());
        EntitySyncStatus belloSyncStatus = statuses.stream().filter(s -> s.getId().equals(savedBello.getId().toString())).findFirst().get();
        Assertions.assertEquals(belloSyncStatus.getStatus(), SyncStatus.UPDATED);

        List<Long> idsToSync = Lists.newArrayList(Long.valueOf(belloSyncStatus.getId()));

        RapidSecurityContext.setAuthenticated(TestPrincipal.create(KAHN));
        List<ReadPetDto> updatedPets= petController.findSome2xx(idsToSync);
        RapidSecurityContext.clear();

        Assertions.assertEquals(1,updatedPets.size());

        ReadPetDto belloDto = updatedPets.stream().filter(s -> s.getId().equals(savedBello.getId())).findFirst().get();
        Assertions.assertEquals(belloDto.getBirthDate(),updatedBello.getBirthDate());
    }

    @Test
    public void givenToyRemovedFromAuditedFieldToysOfPet_whenFetchSyncStatusOfPet_thenMarkedAsUpdated() throws Exception {
        // create bello with ball and rubber-duck toys
        // update bello - remove ball toy
        // check sync status of bello -> marked as updated
        // verify update

        Toy rubberDuck = toyRepository.save(this.rubberDuck);
        Toy ball = toyRepository.save(this.ball);

        ReadPetDto belloDto = createPetLinkedToOwnerAndToys(bello, null, rubberDuck, ball);

        assertPetHasToys(BELLO,RUBBER_DUCK,BALL);
        assertToyHasPet(RUBBER_DUCK,BELLO);
        assertToyHasPet(BALL,BELLO);

        // now
        Timestamp clientUpdate = new Timestamp(new Date().getTime());

        ownerSyncController.fetchSyncStatusesSinceTs_assertNoUpdates(clientUpdate);

        // update bello and bella
        Pet updatedBello = transactionTemplate.execute(new TransactionCallback<Pet>() {
            @Nullable
            @Override
            public Pet doInTransaction(TransactionStatus status) {
                Pet update = petRepository.findById(bello.getId()).get();
                update.removeToy(ball);
                return update;
            }
        });

        Assertions.assertTrue(updatedBello.getLastModifiedDate().after(clientUpdate));


        EntitySyncStatus status = petSyncController.fetchSyncStatus_assertUpdate(belloDto.getId(), clientUpdate, SyncStatus.UPDATED);

        List<Long> idsToSync = Lists.newArrayList(Long.valueOf(status.getId()));

        RapidSecurityContext.setAuthenticated(TestPrincipal.create(KAHN));
        List<ReadPetDto> updatedPets= petController.findSome2xx(idsToSync);
        RapidSecurityContext.clear();

        Assertions.assertEquals(1,updatedPets.size());

        ReadPetDto dto = updatedPets.stream().filter(s -> s.getId().equals(belloDto.getId())).findFirst().get();
        Assertions.assertEquals(1,dto.getToyIds().size());
        Assertions.assertTrue(dto.getToyIds().stream().anyMatch(toyId -> toyId.equals(rubberDuck.getId())));
    }

    @Test
    public void givenToyRemovedViaRemove_whenFetchSyncStatusOfPet_thenNotMarkedAsUpdated() throws Exception {
        // create bello with ball and rubber-duck toys
        // remove ball toy via toyService.remove
        // check sync status of bello -> not marked as updated

        Toy rubberDuck = toyRepository.save(this.rubberDuck);
        Toy ball = toyRepository.save(this.ball);

        ReadPetDto belloDto = createPetLinkedToOwnerAndToys(bello, null, rubberDuck, ball);

        assertPetHasToys(BELLO,RUBBER_DUCK,BALL);
        assertToyHasPet(RUBBER_DUCK,BELLO);
        assertToyHasPet(BALL,BELLO);

        // now
        Timestamp clientUpdate = new Timestamp(new Date().getTime());

        ownerSyncController.fetchSyncStatusesSinceTs_assertNoUpdates(clientUpdate);

        toyRepository.deleteById(ball.getId());
        assertPetHasToys(BELLO,RUBBER_DUCK);
        assertToyHasPet(RUBBER_DUCK,BELLO);


        petSyncController.fetchSyncStatus_assertNoUpdate(belloDto.getId(), clientUpdate);
    }

    // would be overkill to record change in foreignkey column as recorded update for sync
    // just check child set controller for updates with parentId filter
    @Test
    public void givenPetUnlinkedFromOwner_whenFindSyncStatusOfOwner_thenNotMarkedAsUpdated() throws Exception {
        // create owner linked to pet bello and kitty
        // record client ts
        // unlink owners pet bello via update
        // check owner sync info
        // server tells client, owner was not updated
        // fetch update and validate

        Pet savedBello = petRepository.save(bello);
        Pet savedKitty = petRepository.save(kitty);
        Owner owner = createOwnerLinkedToPets(kahn,savedBello.getId(),savedKitty.getId());

        assertOwnerHasPets(KAHN,BELLO,KITTY);
        assertPetHasOwner(BELLO,KAHN);
        assertPetHasOwner(KITTY,KAHN);

        Timestamp lastServerUpdate = new Timestamp(owner.getLastModifiedDate().getTime());

        // now
        Timestamp clientUpdate = new Timestamp(new Date().getTime());
        Assertions.assertTrue(clientUpdate.after(lastServerUpdate));

        ownerSyncController.fetchSyncStatus_assertNoUpdate(owner.getId(),clientUpdate);

        // update owner by unlinking pet bello
        Owner updatedOwner = transactionTemplate.execute(status -> {
            Owner update = ownerRepository.findById(owner.getId()).get();
            update.removePet(savedBello);
            return update;
        });

        assertOwnerHasPets(KAHN,KITTY);
        assertPetHasOwner(BELLO,null);
        assertPetHasOwner(KITTY,KAHN);


        // has not changed
        Assertions.assertEquals(lastServerUpdate, updatedOwner.getLastModifiedDate());


        ownerSyncController.fetchSyncStatus_assertNoUpdate(owner.getId(), clientUpdate);

        List<Long> idsToSync = Lists.newArrayList(owner.getId());

        RapidSecurityContext.setAuthenticated(TestPrincipal.create(KAHN));
        List<ReadOwnerDto> updatedOwners= ownerController.findSome2xx(idsToSync);
        RapidSecurityContext.clear();

        Assertions.assertEquals(1,updatedOwners.size());

        ReadOwnerDto ownerDto = updatedOwners.stream().filter(s -> s.getId().equals(updatedOwner.getId())).findFirst().get();
        Assertions.assertEquals(1,ownerDto.getPetIds().size());
        Assertions.assertEquals(ownerDto.getPetIds().stream().findFirst().get(),savedKitty.getId());
    }

    @Test
    public void givenAllPetsUnlinkedFromOwner_whenFindSyncStatusOfOwner_thenNotMarkedAsUpdated() throws Exception {
        // create owner linked to pet bello and kitty
        // record client ts
        // unlink all owners pets
        // check owner sync info
        // server tells client, owner was not updated
        // fetch update and validate

        Pet savedBello = petRepository.save(bello);
        Pet savedKitty = petRepository.save(kitty);
        Owner owner = createOwnerLinkedToPets(kahn,savedBello.getId(),savedKitty.getId());

        assertOwnerHasPets(KAHN,BELLO,KITTY);
        assertPetHasOwner(BELLO,KAHN);
        assertPetHasOwner(KITTY,KAHN);

        Timestamp lastServerUpdate = new Timestamp(owner.getLastModifiedDate().getTime());

        // now
        Timestamp clientUpdate = new Timestamp(new Date().getTime());
        Assertions.assertTrue(clientUpdate.after(lastServerUpdate));

        ownerSyncController.fetchSyncStatus_assertNoUpdate(owner.getId(),clientUpdate);

        // update owner by unlinking all pets
        Owner updatedOwner = transactionTemplate.execute(status -> {
            Owner update = ownerRepository.findById(owner.getId()).get();
            Iterator<Pet> iterator = update.getPets().iterator();
            while (iterator.hasNext()){
                update.removePet(iterator.next());
            }
            return update;
        });

        assertOwnerHasPets(KAHN);
        assertPetHasOwner(BELLO,null);
        assertPetHasOwner(KITTY,null);


        // has not changed
        Assertions.assertEquals(lastServerUpdate, updatedOwner.getLastModifiedDate());

        ownerSyncController.fetchSyncStatus_assertNoUpdate(owner.getId(),clientUpdate);
    }




}
