package com.github.vincemann.springrapid.synctest;

import com.github.vincemann.springrapid.core.service.filter.jpa.QueryFilter;
import com.github.vincemann.springrapid.coretest.controller.UrlExtension;
import com.github.vincemann.springrapid.coretest.util.RapidTestUtil;
import com.github.vincemann.springrapid.sync.controller.EntitySyncStatusSerializer;
import com.github.vincemann.springrapid.sync.controller.SyncEntityController;
import com.github.vincemann.springrapid.sync.model.EntityLastUpdateInfo;
import com.github.vincemann.springrapid.sync.model.EntitySyncStatus;
import com.github.vincemann.springrapid.sync.model.SyncStatus;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

@Getter
public abstract class AbstractSyncControllerTestTemplate<C extends SyncEntityController> {

    protected MockMvc mvc;
    protected C controller;
    
    protected EntitySyncStatusSerializer syncStatusSerializer;
    protected ApplicationContext applicationContext;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Autowired
    public void setSyncStatusSerializer(EntitySyncStatusSerializer syncStatusSerializer) {
        this.syncStatusSerializer = syncStatusSerializer;
    }

    @Autowired
    public void setController(C controller) {
        this.controller = controller;
    }

    public void setMvc(MockMvc mvc) {
        this.mvc = mvc;
    }

    public EntitySyncStatus fetchSyncStatus_assertUpdate(Long entityId, Date lastClientUpdate, SyncStatus expectedStatus) throws Exception {

        String responseString = mvc.perform(MockMvcRequestBuilders.get(controller.getFetchEntitySyncStatusUrl())
                .param("id", entityId.toString())
                .param("ts", String.valueOf(lastClientUpdate.getTime())))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn().getResponse().getContentAsString();


        EntitySyncStatus status = syncStatusSerializer.deserialize(responseString);
        assert status.getStatus().equals(expectedStatus);
        assert status.getId().equals(entityId.toString());
        return status;
    }

    public void fetchSyncStatus_assertNoUpdate(Long entityId, Date lastClientUpdate) throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(controller.getFetchEntitySyncStatusUrl())
                .param("id", entityId.toString())
                .param("ts", String.valueOf(lastClientUpdate.getTime())))
                .andExpect(MockMvcResultMatchers.status().is(204))
                .andExpect(MockMvcResultMatchers.content().string(""));
    }

    public void fetchSyncStatusesSinceTs_assertNoUpdates(Date clientUpdate, UrlExtension... jpqlFilters) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controller.getFetchEntitySyncStatusesSinceTsUrl())
                .param("ts", String.valueOf(clientUpdate.getTime()));
        if (jpqlFilters.length != 0){
            for (UrlExtension filter : jpqlFilters) {
                assert QueryFilter.class.isAssignableFrom(filter.getExtensionType());
            }
            RapidTestUtil.addUrlExtensionsToRequest(applicationContext,requestBuilder,jpqlFilters);
        }
        mvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().is(204))
                .andExpect(MockMvcResultMatchers.content().string(""));
    }


    public Set<EntitySyncStatus> fetchSyncStatusesSinceTs_assertUpdates(Timestamp clientUpdate, UrlExtension... jpqlFilters) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controller.getFetchEntitySyncStatusesSinceTsUrl())
                .param("ts", String.valueOf(clientUpdate.getTime()));
        if (jpqlFilters.length != 0){
            for (UrlExtension filter : jpqlFilters) {
                assert QueryFilter.class.isAssignableFrom(filter.getExtensionType());
            }
            RapidTestUtil.addUrlExtensionsToRequest(applicationContext,requestBuilder,jpqlFilters);
        }
        String responseString = mvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn().getResponse().getContentAsString();

        return syncStatusSerializer.deserializeToSet(responseString);
    }

    public Set<EntitySyncStatus> fetchSyncStatuses_assertUpdates(Set<EntityLastUpdateInfo> updateInfos) throws Exception {
        String jsonUpdateInfos = getController().getJsonMapper().writeDto(updateInfos);
        String responseString = mvc.perform(MockMvcRequestBuilders.post(controller.getFetchEntitySyncStatusesUrl())
                .content(jsonUpdateInfos).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn().getResponse().getContentAsString();

        return syncStatusSerializer.deserializeToSet(responseString);
    }

    public void fetchSyncStatuses_assertNoUpdates(Set<EntityLastUpdateInfo> updateInfos) throws Exception {
        String jsonUpdateInfos = getController().getJsonMapper().writeDto(updateInfos);
        mvc.perform(MockMvcRequestBuilders.post(controller.getFetchEntitySyncStatusesUrl())
                .content(jsonUpdateInfos))
                .andExpect(MockMvcResultMatchers.status().is(204))
                .andExpect(MockMvcResultMatchers.content().string(""));
    }
}
