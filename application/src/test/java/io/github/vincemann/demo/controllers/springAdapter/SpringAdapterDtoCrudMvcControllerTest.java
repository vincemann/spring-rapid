package io.github.vincemann.demo.controllers.springAdapter;

import io.github.vincemann.generic.crud.lib.config.SpringAdapterDtoCrudControllerConfig;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.DtoMapper;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.DtoMappingContext;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.EndpointsExposureContext;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.idFetchingStrategy.IdFetchingStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.mediaTypeStrategy.MediaTypeStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.mediaTypeStrategy.ProcessDtoException;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.validationStrategy.ValidationStrategy;
import io.github.vincemann.generic.crud.lib.service.EndpointService;
import io.github.vincemann.generic.crud.lib.test.controller.MvcControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles(value = {"test", "springdatajpa"})
//manually add only the autoConfiguration rly needed
@SpringJUnitWebConfig(
        {
                ExampleController.class,
                EndpointService.class,
                WebMvcAutoConfiguration.class,
                SpringAdapterDtoCrudControllerConfig.class,
                ValidationAutoConfiguration.class
        })
//override config to define mock rules before context initialization
@Import(SpringAdapterDtoCrudMvcControllerTest.TestConfig.class)
class SpringAdapterDtoCrudMvcControllerTest
        extends MvcControllerTest<ExampleService, ExampleEntity, Long> {

    @SpyBean
    ExampleController exampleController;

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

    @Autowired//should autowire mock in see below
    MediaTypeStrategy mediaTypeStrategy;
    @Autowired
    MockHttpServletRequest mockHttpServletRequest;

//    @Rule
//    public MockitoRule mockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    MockMvc mockMvc;

    static final ExampleEntity testEntity = new ExampleEntity("testEntity");
    static final ExampleEntity savedTestEntity = new ExampleEntity("saved testEntity");
    static final ExampleReadDto testDto = new ExampleReadDto("read testDto");
    static final ExampleWriteDto savedTestDto = new ExampleWriteDto("saved write TestDto");
    static final String serializedTestEntity = "{ExampleEntity : name : testEntity } ";

    @TestConfiguration
    public static class TestConfig  {

        @Bean
        @Primary
        public MediaTypeStrategy mediaTypeStrategy() throws Exception {
            MediaTypeStrategy mediaTypeStrategy = Mockito.mock(MediaTypeStrategy.class);
            when(mediaTypeStrategy.getMediaType()).thenReturn(MediaType.APPLICATION_JSON_VALUE);
            when(mediaTypeStrategy.writeDto(testEntity)).thenReturn(serializedTestEntity);
            return mediaTypeStrategy;
        }

        @Bean
        @Primary
        public DtoMappingContext getDtoMappingContext(){
            return Mockito.mock(DtoMappingContext.class);
        }

    }

    @BeforeEach
    void setUp(WebApplicationContext wac) {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultRequest(get("/").accept(MediaType.APPLICATION_JSON_UTF8))
                .alwaysExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .alwaysDo(print())
                .build();
        Long testId = 42L;
        savedTestEntity.setId(testId);
        savedTestDto.setId(testId);
    }

    @Test
    void findAll() {

    }

    @Test
    void successfulFind() {

    }

    @Test
    void create_shouldSucceed() throws Exception {
        Class createRequestDtoClass = ExampleReadDto.class;
        Class createReturnDtoClass = ExampleWriteDto.class;
        when(dtoMappingContext.getCreateRequestDtoClass()).thenReturn(createRequestDtoClass);
        when(mediaTypeStrategy.readDto(anyString(), eq(createRequestDtoClass))).thenReturn(testDto);
        when(dtoMapper.mapToEntity(testDto, getController().getEntityClass())).thenReturn(testEntity);
        when(service.save(testEntity)).thenReturn(savedTestEntity);
        when(dtoMappingContext.getCreateReturnDtoClass()).thenReturn(createReturnDtoClass);
        when(dtoMapper.mapToDto(savedTestEntity, createReturnDtoClass)).thenReturn(savedTestDto);

        performCreate(testEntity)
                .andExpect(status().isOk());

        verify(exampleController).create(mockHttpServletRequest);
        verify(dtoMappingContext).getCreateRequestDtoClass();
        verify(dtoMappingContext).getCreateReturnDtoClass();
        verify(mediaTypeStrategy).readDto(anyString(), eq(createRequestDtoClass));
        verify(validationStrategy).validateDto(testDto, mockHttpServletRequest);
        verify(exampleController).beforeCreate(testDto, mockHttpServletRequest);
        verify(dtoMapper).mapToEntity(testDto, getController().getEntityClass());
        verify(service).save(testEntity);
        verify(dtoMapper).mapToDto(savedTestEntity, createReturnDtoClass);

        //verify interactions
//        verifyNoMoreInteractions(mediaTypeStrategy);
//        verifyNoMoreInteractions(validationStrategy);
//        verifyNoMoreInteractions(dtoMapper);
//        verifyNoMoreInteractions(exampleController);
//        verifyNoMoreInteractions(dtoMappingContext);
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }


}