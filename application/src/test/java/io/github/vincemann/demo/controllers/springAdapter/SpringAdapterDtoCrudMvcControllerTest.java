package io.github.vincemann.demo.controllers.springAdapter;

import io.github.vincemann.generic.crud.lib.config.SpringAdapterDtoCrudControllerConfig;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.DtoMapper;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.DtoMappingContext;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.idFetchingStrategy.IdFetchingStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.idFetchingStrategy.exception.IdFetchingException;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.mediaTypeStrategy.MediaTypeStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.validationStrategy.ValidationStrategy;
import io.github.vincemann.generic.crud.lib.service.EndpointService;
import io.github.vincemann.generic.crud.lib.test.controller.MvcControllerTest;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.servlet.http.HttpServletRequest;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles(value = {"test", "springdatajpa"})
//manually add only the autoConfiguration rly needed
@SpringJUnitWebConfig(
        {
                ExampleController.class,
                EndpointService.class,
                WebMvcAutoConfiguration.class,
                SpringAdapterDtoCrudControllerConfig.class,
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

    @MockBean
    DtoMapper dtoMapper;

    @MockBean
    ValidationStrategy<Long> validationStrategy;

    @MockBean
    IdFetchingStrategy<Long> idFetchingStrategy;

    @Autowired//should autowire mock see below
    MediaTypeStrategy mediaTypeStrategy;

    @Autowired
    MockHttpServletRequest mockHttpServletRequest;


    Class readDtoClass = ExampleReadDto.class;
    Class writeDtoClass = ExampleWriteDto.class;

    static final ExampleEntity requestEntity = new ExampleEntity("request testEntity");
    static final ExampleEntity returnEntity = new ExampleEntity("return testEntity");
    static final ExampleReadDto requestDto = new ExampleReadDto("request testDto");
    static final ExampleWriteDto returnDto = new ExampleWriteDto("return TestDto");
    static final Long entityId = 42L;
    static final String serializedRequestEntity = "{ExampleEntity : name : testEntity } ";

    @TestConfiguration
    public static class TestConfig  {

        @Bean
        @Primary
        public MediaTypeStrategy mediaTypeStrategy() throws Exception {
            MediaTypeStrategy mediaTypeStrategy = Mockito.mock(MediaTypeStrategy.class);
            when(mediaTypeStrategy.getMediaType()).thenReturn(MediaType.APPLICATION_JSON_VALUE);
            when(mediaTypeStrategy.writeDto(requestDto)).thenReturn(serializedRequestEntity);
            return mediaTypeStrategy;
        }

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
    void findAll() {

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
        when(service.update(requestEntity,full)).thenReturn(returnEntity);
        when(mediaTypeStrategy.readDto(anyString(), eq(readDtoClass))).thenReturn(requestDto);
        when(dtoMapper.mapToEntity(requestDto, getController().getEntityClass())).thenReturn(requestEntity);
        when(dtoMappingContext.getUpdateReturnDtoClass()).thenReturn(writeDtoClass);
        when(dtoMapper.mapToDto(returnEntity, writeDtoClass)).thenReturn(returnDto);

        //when
        performPartialUpdate(requestDto).andExpect(status().isOk());

        //then
        verify(controllerSpy).update(any(HttpServletRequest.class));
        verify(mediaTypeStrategy).readDto(anyString(), eq(readDtoClass));
        verify(validationStrategy).validateDto(eq(requestDto), any(HttpServletRequest.class));
        verify(controllerSpy).beforeUpdate(eq(requestDto), any(HttpServletRequest.class),eq(full));
        verify(dtoMapper).mapToEntity(requestDto, getController().getEntityClass());
        verify(service).update(requestEntity,full);
        verify(dtoMapper).mapToDto(returnEntity, writeDtoClass);

        verifyNoMoreInteractions(validationStrategy);
        verifyNoMoreInteractions(dtoMapper);
        verifyNoMoreInteractions(service);
    }

    @Test
    void create_shouldSucceed() throws Exception {
        //given
        when(service.save(requestEntity)).thenReturn(returnEntity);
        when(dtoMappingContext.getCreateRequestDtoClass()).thenReturn(readDtoClass);
        when(mediaTypeStrategy.readDto(anyString(), eq(readDtoClass))).thenReturn(requestDto);
        when(dtoMapper.mapToEntity(requestDto, getController().getEntityClass())).thenReturn(requestEntity);
        when(dtoMappingContext.getCreateReturnDtoClass()).thenReturn(writeDtoClass);
        when(dtoMapper.mapToDto(returnEntity, writeDtoClass)).thenReturn(returnDto);

        //when
        performCreate(requestDto)
                .andExpect(status().isOk());

        //then
        verify(controllerSpy).create(any(HttpServletRequest.class));
        verify(mediaTypeStrategy).readDto(anyString(), eq(readDtoClass));
        verify(validationStrategy).validateDto(eq(requestDto), any(HttpServletRequest.class));
        verify(controllerSpy).beforeCreate(eq(requestDto), any(HttpServletRequest.class));
        verify(dtoMapper).mapToEntity(requestDto, getController().getEntityClass());
        verify(service).save(requestEntity);
        verify(dtoMapper).mapToDto(returnEntity, writeDtoClass);

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
                .contentType(getController().getMediaTypeStrategy().getMediaType())
                .accept(getController().getMediaTypeStrategy().getMediaType()))
                .andExpect(status().isOk());


        verify(idFetchingStrategy).fetchId(any());
        verify(validationStrategy).validateId(eq(entityId),any());
        verify(controllerSpy).beforeDelete(eq(entityId),any());
        verify(controllerSpy).delete(any(HttpServletRequest.class));
        verify(service).deleteById(entityId);
    }



}