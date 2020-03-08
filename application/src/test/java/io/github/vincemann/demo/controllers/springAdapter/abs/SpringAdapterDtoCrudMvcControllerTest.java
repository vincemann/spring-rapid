package io.github.vincemann.demo.controllers.springAdapter.abs;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.vincemann.generic.crud.lib.config.CrudControllerConfig;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.DtoMapper;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.DtoMappingContext;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.idFetchingStrategy.IdFetchingStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.validationStrategy.ValidationStrategy;
import io.github.vincemann.generic.crud.lib.service.EndpointService;
import io.github.vincemann.generic.crud.lib.test.controller.MvcControllerTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles(value = {"test"})
//manually add only the autoConfiguration rly needed
@SpringJUnitWebConfig(
        {
                ExampleController.class,
                WebMvcAutoConfiguration.class,
                CrudControllerConfig.class,
                ValidationAutoConfiguration.class,
                PropertyPlaceholderAutoConfiguration.class
        })
//override config to define mock rules before context initialization
@Import(SpringAdapterDtoCrudMvcControllerTest.TestConfig.class)
@PropertySource({"classpath:application.properties","classpath:application-test.properties"})
class SpringAdapterDtoCrudMvcControllerTest
        extends MvcControllerTest<ExampleService, ExampleEntity, Long> {

    @SpyBean
    ExampleController controllerSpy;

    @MockBean
    ExampleService service;

    @Autowired
    DtoMappingContext dtoMappingContext;

    @SpyBean
    DtoMapper dtoMapper;

    @MockBean
    ValidationStrategy<Long> validationStrategy;

    @MockBean
    IdFetchingStrategy<Long> idFetchingStrategy;

    @Autowired
    MockHttpServletRequest mockHttpServletRequest;

    @SpyBean
    ObjectMapper objectMapper;


    Class readDtoClass = ExampleReadDto.class;
    Class writeDtoClass = ExampleWriteDto.class;

    static final ExampleEntity requestEntity = new ExampleEntity("request testEntity");
    static final ExampleEntity returnEntity = new ExampleEntity("return testEntity");
    static final ExampleReadDto requestDto = new ExampleReadDto("request testDto");
    static final ExampleWriteDto returnDto = new ExampleWriteDto("return TestDto");
    static final Long entityId = 42L;
    static final String jsonReturnDto = "{ExampleReturnDto : name : returnDto } ";

    @TestConfiguration
    public static class TestConfig  {


        @Bean
        @Primary
        public DtoMappingContext getDtoMappingContext(){
            return Mockito.mock(DtoMappingContext.class);
        }

    }

    @BeforeEach
    void setUp() throws Exception {
        returnEntity.setId(entityId);
        returnDto.setId(entityId);
    }

    @Test
    void findAll_shouldSucceed() throws Exception {
        when(service.findAll())
                .thenReturn(new HashSet<>(Arrays.asList(returnEntity)));
        when(dtoMappingContext.getFindAllReturnDtoClass())
                .thenReturn(readDtoClass);
        when(dtoMapper.mapToDto(returnEntity,readDtoClass))
                .thenReturn(returnDto);
        when(objectMapper.writeValueAsString(eq(new HashSet<>(Arrays.asList(returnDto)))))
                .thenReturn(jsonReturnDto);

        performFindAll()
                .andExpect(status().isOk())
                .andExpect(content().string(jsonReturnDto));

        verify(dtoMapper).mapToDto(returnEntity,readDtoClass);
        verify(objectMapper).writeValueAsString(eq(new HashSet<>(Arrays.asList(returnDto))));
        verify(controllerSpy).beforeFindAll(any());
        verify(service).findAll();
    }

    @Test
    void fullUpdate_shouldSucceed() throws Exception {
        when(dtoMappingContext.getFullUpdateRequestDtoClass()).thenReturn(readDtoClass);
        update_shouldSucceed(true);
    }

    @Test
    void partialUpdate_shouldSucceed() throws Exception {
        when(dtoMappingContext.getPartialUpdateRequestDtoClass()).thenReturn(readDtoClass);
        update_shouldSucceed(false);
    }

    void update_shouldSucceed(boolean full) throws Exception {
        //given
        when(service.update(requestEntity,full))
                .thenReturn(returnEntity);
        doReturn(requestDto)
                .when(objectMapper).readValue(Mockito.anyString(), Mockito.eq(readDtoClass));
        when(dtoMapper.mapToEntity(requestDto, getController().getEntityClass()))
                .thenReturn(requestEntity);
        when(dtoMappingContext.getUpdateReturnDtoClass())
                .thenReturn(writeDtoClass);
        when(dtoMapper.mapToDto(returnEntity, writeDtoClass))
                .thenReturn(returnDto);
        when(objectMapper.writeValueAsString(returnDto))
                .thenReturn(jsonReturnDto);

        //when
        performUpdate(requestDto,full)
                .andExpect(status().isOk())
                .andExpect(content().string(jsonReturnDto));

        //then
        verify(controllerSpy).update(any(HttpServletRequest.class));
        verify(objectMapper).readValue(anyString(), eq(readDtoClass));
        verify(validationStrategy).validateDto(eq(requestDto), any(HttpServletRequest.class));
        verify(controllerSpy).beforeUpdate(eq(requestDto), any(HttpServletRequest.class),eq(full));
        verify(dtoMapper).mapToEntity(requestDto, getController().getEntityClass());
        verify(service).update(requestEntity,full);
        verify(dtoMapper).mapToDto(returnEntity, writeDtoClass);
        verify(objectMapper,atLeastOnce()).writeValueAsString(returnDto);

        verifyNoMoreInteractions(validationStrategy);
        verifyNoMoreInteractions(dtoMapper);
        //verifyNoMoreInteractions(service);
    }

    @Test
    void create_shouldSucceed() throws Exception {
        //given
        when(service.save(requestEntity)).thenReturn(returnEntity);
        when(dtoMappingContext.getCreateRequestDtoClass()).thenReturn(readDtoClass);
        doReturn(requestDto)
                .when(objectMapper).readValue(Mockito.anyString(), Mockito.eq(readDtoClass));
        when(dtoMapper.mapToEntity(requestDto, getController().getEntityClass())).thenReturn(requestEntity);
        when(dtoMappingContext.getCreateReturnDtoClass()).thenReturn(writeDtoClass);
        when(dtoMapper.mapToDto(returnEntity, writeDtoClass)).thenReturn(returnDto);
        when(objectMapper.writeValueAsString(returnDto)).thenReturn(jsonReturnDto);

        //when
        performCreate(requestDto)
                .andExpect(status().isOk())
                .andExpect(content().string(jsonReturnDto));

        //then
        verify(controllerSpy).create(any(HttpServletRequest.class));
        verify(objectMapper).readValue(anyString(), eq(readDtoClass));
        verify(validationStrategy).validateDto(eq(requestDto), any(HttpServletRequest.class));
        verify(controllerSpy).beforeCreate(eq(requestDto), any(HttpServletRequest.class));
        verify(dtoMapper).mapToEntity(requestDto, getController().getEntityClass());
        verify(service).save(requestEntity);
        verify(dtoMapper).mapToDto(returnEntity, writeDtoClass);
        verify(objectMapper,atLeastOnce()).writeValueAsString(returnDto);

        verifyNoMoreInteractions(validationStrategy);
        verifyNoMoreInteractions(dtoMapper);
        verifyNoMoreInteractions(service);
    }

    @Test
    void find_shouldSucceed() throws Exception {
        when(idFetchingStrategy.fetchId(any())).thenReturn(entityId);
        when(service.findById(entityId)).thenReturn(Optional.of(returnEntity));
        when(dtoMappingContext.getFindReturnDtoClass()).thenReturn(writeDtoClass);
        when(dtoMapper.mapToDto(returnEntity, writeDtoClass)).thenReturn(returnDto);


        //when
        getMockMvc().perform(get(getFindUrl()))
                .andExpect(status().isOk());

        verify(controllerSpy).find(any(HttpServletRequest.class));
        verify(idFetchingStrategy).fetchId(any());
        verify(controllerSpy).beforeFind(eq(entityId),any());
        verify(validationStrategy).validateId(eq(entityId),any());
        verify(service).findById(entityId);
        verify(dtoMappingContext).getFindReturnDtoClass();
        verify(dtoMapper).mapToDto(returnEntity, writeDtoClass);


        verifyNoMoreInteractions(validationStrategy);
        verifyNoMoreInteractions(dtoMapper);
        verifyNoMoreInteractions(service);
    }

    @Test
    void delete_shouldSucceed() throws Exception {
        when(idFetchingStrategy.fetchId(any())).thenReturn(entityId);

        getMockMvc().perform(delete(getDeleteUrl())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().string(""));


        verify(idFetchingStrategy).fetchId(any());
        verify(validationStrategy).validateId(eq(entityId),any());
        verify(controllerSpy).beforeDelete(eq(entityId),any());
        verify(controllerSpy).delete(any(HttpServletRequest.class));
        verify(service).deleteById(entityId);
    }

    @AfterEach
    void tearDown() {
        Mockito.clearInvocations(dtoMappingContext);
    }
}