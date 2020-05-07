package io.github.vincemann.springrapid.demo.lib.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.vincemann.springrapid.core.config.DtoMapperAutoConfiguration;
import io.github.vincemann.springrapid.core.config.RapidControllerAutoConfiguration;
import io.github.vincemann.springrapid.core.controller.dtoMapper.Delegating;
import io.github.vincemann.springrapid.core.controller.dtoMapper.DtoMapper;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.RapidDtoEndpoint;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.Direction;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingContext;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingInfo;
import io.github.vincemann.springrapid.core.controller.rapid.idFetchingStrategy.IdFetchingStrategy;
import io.github.vincemann.springrapid.core.controller.rapid.validationStrategy.ValidationStrategy;
import io.github.vincemann.springrapid.core.util.Lists;
import io.github.vincemann.springrapid.coretest.controller.rapid.AbstractMvcRapidControllerTest;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles(value = {"test"})
//manually add only the autoConfiguration rly needed
@SpringJUnitWebConfig(
        {
                ExampleRapidController.class,
                WebMvcAutoConfiguration.class,
                RapidControllerAutoConfiguration.class,
                ValidationAutoConfiguration.class,
                PropertyPlaceholderAutoConfiguration.class,
                DtoMapperAutoConfiguration.class
        })
//override config to define mock rules before context initialization
@Import(RapidControllerTest.TestConfig.class)
//@PropertySource({"classpath:application.properties","classpath:application-test.properties"})
class RapidControllerTest
        extends AbstractMvcRapidControllerTest<ExampleService, ExampleEntity, Long> {

    static final ExampleEntity requestEntity = new ExampleEntity("request testEntity");
    static final ExampleEntity returnEntity = new ExampleEntity("return testEntity");
    static final String updatedName = "new Name";
    static final String updatePatch = "[{\"op\": \"replace\", \"path\": \"/name\", \"value\":\"" + updatedName + "\"}]";
    static final ExampleReadDto returnDto = new ExampleReadDto("return TestDto");
    static final ExampleWriteDto requestDto = new ExampleWriteDto("request Dto");
    static final Long entityId = 42L;
    static final String jsonReturnDto = "{ExampleReturnDto : name : returnDto } ";
    @SpyBean
    ExampleRapidController controllerSpy;
    @MockBean
    ExampleService service;
    @Autowired
    DtoMappingContext dtoMappingContext;
    @SpyBean
    @Delegating
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

    @BeforeEach
    void setUp() throws Exception {
        super.setup();
        returnEntity.setId(entityId);
        returnDto.setId(entityId);

    }

    @Test
    void findAll_shouldSucceed() throws Exception {
        DtoMappingInfo expectedResponseMappingInfo = DtoMappingInfo.builder()
                .direction(Direction.RESPONSE)
                .endpoint(RapidDtoEndpoint.FIND_ALL)
                .authorities(new ArrayList<>())
                .build();
        when(service.findAll())
                .thenReturn(new HashSet<>(Lists.newArrayList(returnEntity)));
        when(dtoMappingContext.find(eq(expectedResponseMappingInfo)))
                .thenReturn(readDtoClass);
        when(dtoMapper.mapToDto(returnEntity, readDtoClass))
                .thenReturn(returnDto);
        when(objectMapper.writeValueAsString(eq(new HashSet<>(Lists.newArrayList(returnDto)))))
                .thenReturn(jsonReturnDto);

        getMockMvc().perform(findAll())
                .andExpect(status().isOk())
                .andExpect(content().string(jsonReturnDto));

        verify(dtoMapper).mapToDto(returnEntity, readDtoClass);
        verify(objectMapper).writeValueAsString(eq(new HashSet<>(Lists.newArrayList(returnDto))));
        verify(controllerSpy).beforeFindAll(any(HttpServletRequest.class), any(HttpServletResponse.class));
        verify(service).findAll();
        verifyDtoMappingContextInteraction(expectedResponseMappingInfo);
    }

    //todo add test for invalid patch
    @Test
    @Disabled //ich werde sicherlich noch mal was an der aktuellen update methode machen
    void update_shouldSucceed() throws Exception {
        DtoMappingInfo expectedRequestMappingInfo = DtoMappingInfo.builder()
                .direction(Direction.REQUEST)
                .endpoint(RapidDtoEndpoint.UPDATE)
                .authorities(new ArrayList<>())
                .build();
        DtoMappingInfo expectedResponseMappingInfo = DtoMappingInfo.builder()
                .direction(Direction.RESPONSE)
                .endpoint(RapidDtoEndpoint.UPDATE)
                .authorities(new ArrayList<>())
                .build();

        ExampleEntity oldEntity = new ExampleEntity();
        oldEntity.setName("old");

        ExampleEntity patch = new ExampleEntity();
        patch.setName(updatedName);

        ExampleReadDto patchDto =new ExampleReadDto();
        patchDto.setName(updatedName);

        when(idFetchingStrategy.fetchId(any(HttpServletRequest.class)))
                .thenReturn(entityId);

        when(dtoMappingContext.find(eq(expectedRequestMappingInfo)))
                .thenReturn(readDtoClass);
        when(dtoMappingContext.find(eq(expectedResponseMappingInfo)))
                .thenReturn(writeDtoClass);

        when(service.findById(entityId))
                .thenReturn(Optional.of(oldEntity));


        doReturn(patchDto)
                .when(dtoMapper).mapToDto(refEq(patch),eq(readDtoClass));
//        when(dtoMapper.mapToDto(refEq(patch), eq(writeDtoClass)))
//                .thenReturn(patchDto);
        //given
//        doReturn(updatePatch)
//                .when(objectMapper).readValue(Mockito.anyString(), Mockito.eq(readDtoClass));

        when(service.update(refEq(patch), eq(true)))
                .thenReturn(returnEntity);

//        when(dtoMapper.mapToEntity(refEq(ExampleWriteDto.class), getController().getEntityClass()))
//                .thenReturn(oldEntity);
        when(dtoMapper.mapToDto(returnEntity,writeDtoClass))
                .thenReturn(returnDto);

        when(objectMapper.writeValueAsString(returnDto))
                .thenReturn(jsonReturnDto);

        //when
        getMockMvc().perform(put(getUpdateUrl())
                .content(updatePatch)
                .contentType(getContentType()))
                .andExpect(status().isOk())
                .andExpect(content().string(jsonReturnDto));

        //then
        verify(controllerSpy).update(any(HttpServletRequest.class), any(HttpServletResponse.class));
//        verify(objectMapper).readValue(anyString(), eq(readDtoClass));
        verify(validationStrategy).validateDto(refEq(patchDto));
        verify(controllerSpy).beforeUpdate(eq(patchDto.getClass()), eq(entityId), eq(updatePatch), any(HttpServletRequest.class), any(HttpServletResponse.class));
//        verify(dtoMapper).mapToEntity(requestDto, getController().getEntityClass());
//        ArgumentCaptor<ExampleEntity> updateArg = ArgumentCaptor.forClass(ExampleEntity.class);
        verify(service).update(refEq(patch), eq(true));
//        Assertions.assertEquals(updatedName,updateArg.getValue().getName());
        verify(dtoMapper).mapToDto(refEq(patch), eq(readDtoClass));
        verify(dtoMapper).mapToDto(refEq(returnEntity), eq(writeDtoClass));
        verify(objectMapper, atLeastOnce()).writeValueAsString(returnDto);


        verifyNoMoreInteractions(validationStrategy);
        verifyNoMoreInteractions(dtoMapper);

        verifyDtoMappingContextInteraction(expectedRequestMappingInfo, expectedResponseMappingInfo);
    }

    private void verifyDtoMappingContextInteraction(DtoMappingInfo... info) {
        ArgumentCaptor<DtoMappingInfo> argumentCaptor = ArgumentCaptor.forClass(DtoMappingInfo.class);
        verify(dtoMappingContext, times(info.length)).find(argumentCaptor.capture());
        int index = 0;
        for (DtoMappingInfo value : argumentCaptor.getAllValues()) {
            Assertions.assertEquals(info[index], value);
            index++;
        }
    }


//    @Test
//    void partialUpdate_shouldSucceed() throws Exception {
//        DtoMappingInfo expectedRequestMappingInfo = DtoMappingInfo.builder()
//                .direction(Direction.REQUEST)
//                .endpoint(CrudDtoEndpoint.PARTIAL_UPDATE)
//                .authorities(new ArrayList<>())
//                .build();
//        DtoMappingInfo expectedResponseMappingInfo = DtoMappingInfo.builder()
//                .direction(Direction.RESPONSE)
//                .endpoint(CrudDtoEndpoint.PARTIAL_UPDATE)
//                .authorities(new ArrayList<>())
//                .build();
//
//        when(dtoMappingContext.find(eq(expectedRequestMappingInfo)))
//                .thenReturn(readDtoClass);
//
//        when(dtoMappingContext.find(eq(expectedResponseMappingInfo)))
//                .thenReturn(writeDtoClass);
//
//        update_shouldSucceed(false);
//
//        verifyDtoMappingContextInteraction(expectedRequestMappingInfo,expectedResponseMappingInfo);
//    }

    @Test
    void create_shouldSucceed() throws Exception {
        //given

        DtoMappingInfo expectedRequestMappingInfo = DtoMappingInfo.builder()
                .authorities(new ArrayList<>())
                .direction(Direction.REQUEST)
                .endpoint(RapidDtoEndpoint.CREATE)
                .build();
        when(dtoMappingContext.find(eq(expectedRequestMappingInfo)))
                .thenReturn(readDtoClass);

        doReturn(requestDto)
                .when(objectMapper).readValue(Mockito.anyString(), Mockito.eq(readDtoClass));

        when(dtoMapper.mapToEntity(requestDto, getController().getEntityClass()))
                .thenReturn(requestEntity);

        when(service.save(requestEntity)).thenReturn(returnEntity);

        DtoMappingInfo expectedResponseMappingInfo = DtoMappingInfo.builder()
                .authorities(new ArrayList<>())
                .direction(Direction.RESPONSE)
                .endpoint(RapidDtoEndpoint.CREATE)
                .build();

        when(dtoMappingContext.find(eq(expectedResponseMappingInfo)))
                .thenReturn(writeDtoClass);

        when(dtoMapper.mapToDto(returnEntity, writeDtoClass))
                .thenReturn(returnDto);

        when(objectMapper.writeValueAsString(returnDto))
                .thenReturn(jsonReturnDto);

        //when
        getMockMvc().perform(create(requestDto))
                .andExpect(status().isOk())
                .andExpect(content().string(jsonReturnDto));

        //then
        verify(controllerSpy).create(any(HttpServletRequest.class), any(HttpServletResponse.class));
        verify(objectMapper).readValue(anyString(), eq(readDtoClass));
        verify(validationStrategy).validateDto(eq(requestDto));
        verify(controllerSpy).beforeCreate(eq(requestDto), any(HttpServletRequest.class), any(HttpServletResponse.class));
        verify(dtoMapper).mapToEntity(requestDto, getController().getEntityClass());
        verify(service).save(requestEntity);
        verify(dtoMapper).mapToDto(returnEntity, writeDtoClass);
        verify(objectMapper, atLeastOnce()).writeValueAsString(returnDto);
        verifyDtoMappingContextInteraction(expectedRequestMappingInfo, expectedResponseMappingInfo);

        verifyNoMoreInteractions(validationStrategy);
        verifyNoMoreInteractions(dtoMapper);
        verifyNoMoreInteractions(service);
    }

    @Test
    void find_shouldSucceed() throws Exception {
        DtoMappingInfo expectedResponseMappingInfo = DtoMappingInfo.builder()
                .authorities(new ArrayList<>())
                .direction(Direction.RESPONSE)
                .endpoint(RapidDtoEndpoint.FIND)
                .build();

        when(idFetchingStrategy.fetchId(any()))
                .thenReturn(entityId);

        when(service.findById(entityId))
                .thenReturn(Optional.of(returnEntity));

        when(dtoMappingContext.find(eq(expectedResponseMappingInfo)))
                .thenReturn(writeDtoClass);

        when(dtoMapper.mapToDto(returnEntity, writeDtoClass))
                .thenReturn(returnDto);


        //when
        getMockMvc().perform(get(getFindUrl()))
                .andExpect(status().isOk());

        verify(controllerSpy).find(any(HttpServletRequest.class), any(HttpServletResponse.class));
        verify(idFetchingStrategy).fetchId(any());
        verify(controllerSpy).beforeFind(eq(entityId), any(HttpServletRequest.class), any(HttpServletResponse.class));
        verify(validationStrategy).validateId(eq(entityId));
        verify(service).findById(entityId);
        verify(dtoMapper).mapToDto(returnEntity, writeDtoClass);
        verifyDtoMappingContextInteraction(expectedResponseMappingInfo);


        verifyNoMoreInteractions(validationStrategy);
        verifyNoMoreInteractions(dtoMapper);
        verifyNoMoreInteractions(service);
    }

    @Test
    void delete_shouldSucceed() throws Exception {
        when(idFetchingStrategy.fetchId(any())).thenReturn(entityId);

        getMockMvc().perform(delete(getDeleteUrl())
                .contentType(getContentType())
                .accept(getContentType()))
                .andExpect(status().isOk())
                .andExpect(content().string(""));


        verify(idFetchingStrategy).fetchId(any());
        verify(validationStrategy).validateId(eq(entityId));
        verify(controllerSpy).beforeDelete(eq(entityId), any(HttpServletRequest.class), any(HttpServletResponse.class));
        verify(controllerSpy).delete(any(HttpServletRequest.class), any(HttpServletResponse.class));
        verify(service).deleteById(entityId);
    }

    @AfterEach
    void tearDown() {
        Mockito.clearInvocations(dtoMappingContext);
    }

    @TestConfiguration
    public static class TestConfig {
        @Bean
        @Primary
        public DtoMappingContext getDtoMappingContext() {
            return Mockito.mock(DtoMappingContext.class);
        }

    }
}