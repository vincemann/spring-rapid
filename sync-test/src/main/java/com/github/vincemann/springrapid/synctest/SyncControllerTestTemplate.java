package com.github.vincemann.springrapid.synctest;

import com.github.vincemann.springrapid.coretest.controller.template.MvcControllerTestTemplate;
import com.github.vincemann.springrapid.sync.controller.SyncEntityController;
import com.github.vincemann.springrapid.sync.model.EntitySyncStatus;
import com.github.vincemann.springrapid.sync.model.LastFetchInfo;
import com.github.vincemann.springrapid.sync.model.SyncStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


public abstract class SyncControllerTestTemplate<C extends SyncEntityController>
        extends MvcControllerTestTemplate<C> {

    protected ApplicationContext applicationContext;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }


    public MockHttpServletRequestBuilder fetchSyncStatus(Long entityId, Date lastClientUpdate) {
        return MockMvcRequestBuilders.get(controller.getSyncEntityUrl())
                .param("id", entityId.toString())
                .param("ts", String.valueOf(lastClientUpdate.getTime()));
    }


    public MockHttpServletRequestBuilder fetchSyncStatusesSinceTs(Date clientUpdate) {
        return MockMvcRequestBuilders.get(controller.getSyncEntitiesSinceUrl())
                .param("ts",String.valueOf(clientUpdate.getTime()));
    }


    public MockHttpServletRequestBuilder fetchSyncStatuses(List<LastFetchInfo> updateInfos) throws Exception {
        String jsonUpdateInfos = getController().getObjectMapper().writeValueAsString(updateInfos);
        return MockMvcRequestBuilders.post(controller.getSyncEntitiesUrl())
                .content(jsonUpdateInfos).contentType(MediaType.APPLICATION_JSON_UTF8);
    }

    public EntitySyncStatus fetchSyncStatus_assertUpdate(Long entityId, Date lastClientUpdate, SyncStatus expectedStatus) throws Exception {

        String json = mvc.perform(MockMvcRequestBuilders.get(controller.getSyncEntityUrl())
                        .param("id", entityId.toString())
                        .param("ts", String.valueOf(lastClientUpdate.getTime())))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn().getResponse().getContentAsString();


        EntitySyncStatus status = getController().getObjectMapper().readValue(json,EntitySyncStatus.class);
        assertThat(status.getStatus(),equalTo(expectedStatus));
        assertThat(entityId.toString(),equalTo(status.getId()));
        return status;
    }


    public void fetchSyncStatus_assertNoUpdate(Long entityId, Date lastClientUpdate) throws Exception {
        mvc.perform(fetchSyncStatus(entityId,lastClientUpdate))
                .andExpect(MockMvcResultMatchers.status().is(204))
                .andExpect(MockMvcResultMatchers.content().string(""));
    }

    public void fetchSyncStatusesSinceTs_assertNoUpdates(Date clientUpdate) throws Exception {
        mvc.perform(fetchSyncStatusesSinceTs(clientUpdate))
                .andExpect(MockMvcResultMatchers.status().is(204))
                .andExpect(MockMvcResultMatchers.content().string(""));
    }
    


    public List<EntitySyncStatus> fetchSyncStatusesSinceTs_assertUpdates(Timestamp clientUpdate) throws Exception {
        String json = mvc.perform(fetchSyncStatusesSinceTs(clientUpdate))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn().getResponse().getContentAsString();


        return deserializeToList(json,EntitySyncStatus.class);
    }

    public List<EntitySyncStatus> fetchSyncStatuses_assertUpdates(List<LastFetchInfo> updateInfos) throws Exception {
        String json = mvc.perform(fetchSyncStatuses(updateInfos))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn().getResponse().getContentAsString();

        return deserializeToList(json,EntitySyncStatus.class);
    }

    public void fetchSyncStatusesSinceTs_assertNoUpdates(List<LastFetchInfo> updateInfos) throws Exception {
        mvc.perform(fetchSyncStatuses(updateInfos))
                .andExpect(MockMvcResultMatchers.status().is(204))
                .andExpect(MockMvcResultMatchers.content().string(""));
    }


}
