package io.github.vincemann.generic.crud.lib.test.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.vincemann.generic.crud.lib.config.JacksonConfig;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.DtoMappingContext;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.exception.DtoMappingException;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.SpringAdapterJsonDtoCrudController;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.test.InitializingTest;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
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
 * Use this base class to test your Controllers.
 * Service is tested in isolation, that's why it should be mocked here and only the correct interaction
 * with the service is getting tested.
 *
 * Main Goal of this test, is to test the correct setup of the web-layer and that the webcomponents
 * work together as expected.
 * Each component is tested heavily in isolation though, which means, that a few simple test cases here are sufficient.
 * @param <S> Service Type
 * @param <E> Entity managed by Service Type
 * @param <Id> Id Type of Entity
 */
@Getter
@Setter
@Slf4j
//web layer and service is layer is loaded, I cannot get it to work with a single controller loaded and service layer not loaded..
@ActiveProfiles(value = {"test","web","service"})
@SpringBootTest
@AutoConfigureMockMvc
@Import({JacksonConfig.class})
public abstract class MvcControllerTest
        <S extends CrudService<E,Id,? extends CrudRepository<E,Id>>
        ,E extends IdentifiableEntity<Id>,
        Id extends Serializable>
        extends InitializingTest
            implements InitializingBean {

    private static final String LOCAL_HOST = "127.0.0.1";


    private Class<E> entityClass = (Class<E>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    private DtoMappingContext dtoMappingContext;
    private String url;
    private S testService;
    private SpringAdapterJsonDtoCrudController<E, Id> controller;
    private MockMvc mockMvc;

//    @MockBean
//    private S mockedService;
    //load from jackson config, using same mapper, the controllers are using
    //i expect this component to work, thus using it in my test methods as well
    @Autowired
    private ObjectMapper objectMapper;

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
    public void injectController(SpringAdapterJsonDtoCrudController<E, Id> controller) {
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

    public E mapToEntity(IdentifiableEntity<Id> dto) throws DtoMappingException {
        return getController().getDtoMapper().mapToEntity(dto,getEntityClass());
    }

    public ResultActions performCreate(IdentifiableEntity<Id> dto) throws Exception {
        return getMockMvc().perform(
                post(getCreateUrl())
                        .content(serialize(dto))
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

    public <C extends SpringAdapterJsonDtoCrudController<E,Id>> C getController(){
        return (C) controller;
    }


    public <Dto extends IdentifiableEntity<Id>> Dto readDto(MvcResult mvcResult, Class<Dto> dtoClass) throws Exception{
        return deserialize(mvcResult.getResponse().getContentAsString(),dtoClass);
    }


}
