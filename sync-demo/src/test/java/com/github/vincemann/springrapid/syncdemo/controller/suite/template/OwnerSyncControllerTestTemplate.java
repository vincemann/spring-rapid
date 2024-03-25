package com.github.vincemann.springrapid.syncdemo.controller.suite.template;

import com.github.vincemann.springrapid.sync.model.EntitySyncStatus;
import org.springframework.stereotype.Component;
import com.github.vincemann.springrapid.syncdemo.controller.sync.OwnerSyncController;
import com.github.vincemann.springrapid.synctest.SyncControllerTestTemplate;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Date;
import java.util.List;

@Component
public class OwnerSyncControllerTestTemplate extends SyncControllerTestTemplate<OwnerSyncController> {

    public MockHttpServletRequestBuilder fetchSyncStatusesSinceTsOfOwnersWithTelnrPrefix(Date clientUpdate,String telnrPrefix){
        return MockMvcRequestBuilders.get("/api/core/owner/sync-statuses-with-telprefix")
                .param("ts",String.valueOf(clientUpdate.getTime()))
                .param("prefix",telnrPrefix);
    }

    public List<EntitySyncStatus> fetchSyncStatusesSinceTsOfOwnersWithTelnrPrefix_assertUpdates(Date clientUpdate, String telnrPrefix) throws Exception {
        String json = mvc.perform(fetchSyncStatusesSinceTsOfOwnersWithTelnrPrefix(clientUpdate,telnrPrefix))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn().getResponse().getContentAsString();

        return deserializeToList(json,EntitySyncStatus.class);
    }
}
