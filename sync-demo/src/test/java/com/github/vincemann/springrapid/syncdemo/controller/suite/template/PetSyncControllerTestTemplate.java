package com.github.vincemann.springrapid.syncdemo.controller.suite.template;

import com.github.vincemann.springrapid.sync.model.EntitySyncStatus;
import org.springframework.stereotype.Component;
import com.github.vincemann.springrapid.syncdemo.controller.sync.PetSyncController;
import com.github.vincemann.springrapid.synctest.SyncControllerTestTemplate;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Date;
import java.util.List;

@Component
public class PetSyncControllerTestTemplate extends SyncControllerTestTemplate<PetSyncController> {

    public MockHttpServletRequestBuilder fetchSyncStatusesSinceTsOfOwner(Date clientUpdate, long ownerId){
        return MockMvcRequestBuilders.get("/api/core/pet/sync-statuses-since-ts-of-owner")
                .param("ts",String.valueOf(clientUpdate.getTime()))
                .param("owner",String.valueOf(ownerId));
    }

    public List<EntitySyncStatus> fetchSyncStatusesSinceTsOfOwner_assertUpdates(Date clientUpdate, long ownerId) throws Exception {
        String json = mvc.perform(fetchSyncStatusesSinceTsOfOwner(clientUpdate,ownerId))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn().getResponse().getContentAsString();

        return jsonToList(json,EntitySyncStatus.class);
    }
}
