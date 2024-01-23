package com.github.vincemann.springrapid.synctest;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.github.vincemann.springrapid.core.service.filter.EntityFilter;
import com.github.vincemann.springrapid.core.service.filter.jpa.QueryFilter;
import com.github.vincemann.springrapid.coretest.controller.UrlExtension;
import com.github.vincemann.springrapid.coretest.controller.template.MvcControllerTestTemplate;
import com.github.vincemann.springrapid.coretest.util.RapidTestUtil;
import com.github.vincemann.springrapid.sync.controller.SyncEntityController;
import com.github.vincemann.springrapid.sync.model.EntityUpdateInfo;
import com.github.vincemann.springrapid.sync.model.EntitySyncStatus;
import com.github.vincemann.springrapid.sync.model.SyncStatus;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
public abstract class SyncControllerTestTemplate<C extends SyncEntityController>
        extends MvcControllerTestTemplate<C> {

    protected ApplicationContext applicationContext;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }


    public MockHttpServletRequestBuilder fetchSyncStatus(Long entityId, Date lastClientUpdate) {
        return MockMvcRequestBuilders.get(controller.getFetchEntitySyncStatusUrl())
                .param("id", entityId.toString())
                .param("ts", String.valueOf(lastClientUpdate.getTime()));
    }

    public MockHttpServletRequestBuilder fetchSyncStatusesSinceTs(Date clientUpdate, UrlExtension... filters) {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controller.getFetchEntitySyncStatusesSinceTsUrl())
                .param("ts", String.valueOf(clientUpdate.getTime()));
        if (filters.length != 0){
            for (UrlExtension filter : filters) {
                assert QueryFilter.class.isAssignableFrom(filter.getExtensionType()) || EntityFilter.class.isAssignableFrom(filter.getExtensionType());
            }
            RapidTestUtil.addUrlExtensionsToRequest(applicationContext,requestBuilder,filters);
        }
        return requestBuilder;
    }


    public MockHttpServletRequestBuilder fetchSyncStatuses(Set<EntityUpdateInfo> updateInfos) throws Exception {
        String jsonUpdateInfos = getController().getJsonMapper().writeDto(updateInfos);
        return MockMvcRequestBuilders.post(controller.getFetchEntitySyncStatusesUrl())
                .content(jsonUpdateInfos).contentType(MediaType.APPLICATION_JSON);
    }


    public EntitySyncStatus fetchSyncStatus_assertUpdate(Long entityId, Date lastClientUpdate, SyncStatus expectedStatus) throws Exception {

        String json = mvc.perform(MockMvcRequestBuilders.get(controller.getFetchEntitySyncStatusUrl())
                        .param("id", entityId.toString())
                        .param("ts", String.valueOf(lastClientUpdate.getTime())))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn().getResponse().getContentAsString();


        EntitySyncStatus status = getController().getJsonMapper().readDto(json,EntitySyncStatus.class);
        assert status.getStatus().equals(expectedStatus);
        assert status.getId().equals(entityId.toString());
        return status;
    }

    public void fetchSyncStatus_assertNoUpdate(Long entityId, Date lastClientUpdate) throws Exception {
        mvc.perform(fetchSyncStatus(entityId,lastClientUpdate))
                .andExpect(MockMvcResultMatchers.status().is(204))
                .andExpect(MockMvcResultMatchers.content().string(""));
    }

    public void fetchSyncStatusesSinceTs_assertNoUpdates(Date clientUpdate, UrlExtension... jpqlFilters) throws Exception {
        mvc.perform(fetchSyncStatusesSinceTs(clientUpdate,jpqlFilters))
                .andExpect(MockMvcResultMatchers.status().is(204))
                .andExpect(MockMvcResultMatchers.content().string(""));
    }


    public Set<EntitySyncStatus> fetchSyncStatusesSinceTs_assertUpdates(Timestamp clientUpdate, UrlExtension... jpqlFilters) throws Exception {
        String json = mvc.perform(fetchSyncStatusesSinceTs(clientUpdate,jpqlFilters))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn().getResponse().getContentAsString();


        return deserializeToSet(json,EntitySyncStatus.class);
    }

    public Set<EntitySyncStatus> fetchSyncStatuses_assertUpdates(Set<EntityUpdateInfo> updateInfos) throws Exception {
        String json = mvc.perform(fetchSyncStatuses(updateInfos))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn().getResponse().getContentAsString();

        return deserializeToSet(json,EntitySyncStatus.class);
    }

    public void fetchSyncStatuses_assertNoUpdates(Set<EntityUpdateInfo> updateInfos) throws Exception {
        mvc.perform(fetchSyncStatuses(updateInfos))
                .andExpect(MockMvcResultMatchers.status().is(204))
                .andExpect(MockMvcResultMatchers.content().string(""));
    }

    private <Dto> List<Dto> deserializeToList(String s, Class<Dto> dtoClass) throws IOException {
        CollectionType setType = getController().getJsonMapper().getObjectMapper()
                .getTypeFactory().constructCollectionType(List.class, dtoClass);
        return deserialize(s, setType);
    }

    public  <Dto> Set<Dto> deserializeToSet(String s, Class<Dto> dtoClass) throws IOException {
        CollectionType setType = getController().getJsonMapper().getObjectMapper()
                .getTypeFactory().constructCollectionType(Set.class, dtoClass);
        return deserialize(s, setType);
    }

    public  <Dto> Dto deserialize(String s, JavaType dtoClass) throws IOException {
        return (Dto) getController().getJsonMapper().readDto(s,dtoClass);
    }

}
