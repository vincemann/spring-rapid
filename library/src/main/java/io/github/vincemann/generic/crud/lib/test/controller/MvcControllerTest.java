package io.github.vincemann.generic.crud.lib.test.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.DtoMappingContext;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.exception.EntityMappingException;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.SpringAdapterDtoCrudController;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

/**
 * Baseclass for all Controller IntegrationTests
 * @param <E>
 * @param <Id>
 */
@Getter
@Setter
@Slf4j
public abstract class MvcControllerTest
        <S extends CrudService<E,Id,? extends CrudRepository<E,Id>>
        ,E extends IdentifiableEntity<Id>,
        Id extends Serializable>
        //extends InitializingTest
            implements InitializingBean {

    private static final String LOCAL_HOST = "127.0.0.1";


    private Class<E> entityClass = (Class<E>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    private DtoMappingContext dtoMappingContext;
    private String url;
    private S testService;
    private SpringAdapterDtoCrudController<E, Id> controller;
    private MockMvc mockMvc;

    public MvcControllerTest(String url) {
        this.url=url;
    }

    public MvcControllerTest() {
        this(LOCAL_HOST);
    }

    @Autowired
    public void injectTestService(S testService) {
        this.testService = testService;
    }

    @Autowired
    public void injectController(SpringAdapterDtoCrudController<E, Id> controller) {
        this.controller = controller;
    }

    @BeforeEach
    public void setupMvc(WebApplicationContext wac) {
        String mediaType = MediaType.APPLICATION_JSON_UTF8_VALUE;
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultRequest(get("/").accept(mediaType))
                .alwaysExpect(content().contentType(mediaType))
                .alwaysDo(print())
                .build();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //super.afterPropertiesSet();
        //user might want to inject own beans that are diff from controllers beans -> null checks
        if(dtoMappingContext ==null) {
            dtoMappingContext = getController().getDtoMappingContext();
        }
        if (testService == null) {
            setTestService(getController().getCastedCrudService());
        }
    }

    public E map(IdentifiableEntity<Id> dto) throws EntityMappingException {
        return getController().getDtoMapper().mapToEntity(dto,getEntityClass());
    }

    public ResultActions performCreate(IdentifiableEntity<Id> dto) throws Exception {
        return getMockMvc().perform(
                post(getCreateUrl())
                        .content(serialize(dto))
                        //todo this must be set
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
        );
    }

    public ResultActions performUpdate(IdentifiableEntity<Id> updateDto, Boolean full) throws Exception {
        String fullUpdateQueryParam = getController().getFullUpdateQueryParam();

        return getMockMvc().perform(put(getUpdateUrl()+"?"+fullUpdateQueryParam+"="+full.toString())
                .content(serialize(updateDto))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
    }

    public ResultActions performFullUpdate(IdentifiableEntity<Id> updateDto) throws Exception {
        return performUpdate(updateDto,Boolean.TRUE);
    }

    public ResultActions performPartialUpdate(IdentifiableEntity<Id> updateDto) throws Exception {
        return performUpdate(updateDto,Boolean.FALSE);
    }

    public ResultActions performFindAll() throws Exception {
        return getMockMvc().perform(get(getController().getFindAllUrl()));
    }

    public String getCreateUrl(){
        return getController().getCreateUrl();
    }

    public String getFindUrl(){
        return getController().getFindUrl();
    }
    public String getDeleteUrl(){
        return getController().getDeleteUrl();
    }
    public String getUpdateUrl(){
        return getController().getUpdateUrl();
    }

    public String getFindAllUrl(){
        return getController().getFindAllUrl();
    }

    public String serialize(Object o) throws JsonProcessingException {
        return getController().getJsonMapper().writeValueAsString(o);
    }

    public <Dto extends IdentifiableEntity<Id>> Dto deserialize(String s,Class<Dto> dtoClass) throws IOException {
        return getController().getJsonMapper().readValue(s,dtoClass);
    }

    public <C extends SpringAdapterDtoCrudController<E,Id>> C getController(){
        return (C) controller;
    }


    public <Dto extends IdentifiableEntity<Id>> Dto readDto(MvcResult mvcResult, Class<Dto> dtoClass) throws Exception{
        return deserialize(mvcResult.getResponse().getContentAsString(),dtoClass);
    }


}
