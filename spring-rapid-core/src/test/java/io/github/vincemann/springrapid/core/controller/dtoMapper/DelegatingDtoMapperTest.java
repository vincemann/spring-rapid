package io.github.vincemann.springrapid.core.controller.dtoMapper;

import io.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class DelegatingDtoMapperTest {
    DelegatingDtoMapper mapper;
    DtoMapper defaultMapper;

    DtoMapper customEntityMapper;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @ToString
    class KnownEntity extends IdentifiableEntityImpl<Long> {
        private String name;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @ToString
    class KnownDto extends IdentifiableEntityImpl<Long> {
        private String name;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @ToString
    class UnknownEntity extends IdentifiableEntityImpl<Long> {
        private String name;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @ToString
    class UnknownDto extends IdentifiableEntityImpl<Long> {
        private String name;
    }

    @BeforeEach
    void setUp() {
        customEntityMapper = Mockito.mock(DtoMapper.class);
        when(customEntityMapper.isDtoClassSupported(KnownDto.class))
                .thenReturn(true);
        defaultMapper = Mockito.mock(DtoMapper.class);
        mapper = new DelegatingDtoMapper(defaultMapper);
        mapper.registerDelegate(customEntityMapper);
    }

    @Test
    public void mapKnownEntityToDto_shouldDelegateToKnownMapper() throws DtoMappingException {
        KnownEntity entity = new KnownEntity();
        mapper.mapToDto(entity, KnownDto.class);
        verify(customEntityMapper).isDtoClassSupported(KnownDto.class);
        verify(customEntityMapper).mapToDto(entity, KnownDto.class);
        verifyZeroInteractions(defaultMapper);
    }

    @Test
    public void mapKnownDtoToEntity_shouldDelegateToKnownMapper() throws DtoMappingException {
        KnownDto dto = new KnownDto();
        mapper.mapToEntity(dto, KnownEntity.class);
        verify(customEntityMapper).isDtoClassSupported(KnownDto.class);
        verify(customEntityMapper).mapToEntity(dto, KnownEntity.class);
        verifyZeroInteractions(defaultMapper);
    }

    @Test
    public void mapUnknownEntityToDto_shouldDelegateToDefaultMapper() throws DtoMappingException {
        UnknownEntity entity = new UnknownEntity();
        mapper.mapToDto(entity, UnknownDto.class);
        verify(customEntityMapper).isDtoClassSupported(UnknownDto.class);
        verifyNoMoreInteractions(customEntityMapper);
        verify(defaultMapper).mapToDto(entity,UnknownDto.class);
    }


}