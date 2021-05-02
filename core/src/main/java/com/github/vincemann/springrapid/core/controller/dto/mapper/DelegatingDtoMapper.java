package com.github.vincemann.springrapid.core.controller.dto.mapper;


import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional
public class DelegatingDtoMapper{

    private List<DtoMapper<?, ?>> delegates = new ArrayList<>();
    private List<DtoEntityPostProcessor> dtoEntityPostProcessors = new ArrayList<>();
    private List<EntityDtoPostProcessor> entityDtoPostProcessors = new ArrayList<>();

    //@LogInteraction
    public <T extends IdentifiableEntity<?>> T mapToEntity(Object dto, Class<T> destinationClass) throws EntityNotFoundException, BadEntityException {
        T mapped = (T) findMapper(dto.getClass())
                .mapToEntity(dto, destinationClass);
        for (DtoEntityPostProcessor pp : dtoEntityPostProcessors) {
            if (pp.supports(mapped.getClass(), dto.getClass())) {
                pp.postProcessEntity(mapped, dto);
            }
        }
        return mapped;
    }

    public void registerDelegate(DtoMapper delegate) {
        this.delegates.add(delegate);
    }

    public void registerDtoEntityPostProcessor(DtoEntityPostProcessor postProcessor){
        this.dtoEntityPostProcessors.add(postProcessor);
    }

    public void registerEntityDtoPostProcessor(EntityDtoPostProcessor postProcessor){
        this.entityDtoPostProcessors.add(postProcessor);
    }

    //@LogInteraction
    public <T> T mapToDto(IdentifiableEntity<?> source, Class<T> destinationClass) throws BadEntityException {
        T dto = (T) findMapper(destinationClass)
                .mapToDto(source, destinationClass);
        for (EntityDtoPostProcessor pp : entityDtoPostProcessors) {
            if (pp.supports(source.getClass(), dto.getClass())) {
                pp.postProcessDto(dto, source);
            }
        }
        return dto;
    }

    private DtoMapper findMapper(Class<?> dtoClass) {
        Optional<DtoMapper<?, ?>> matchingMapper =
                delegates.stream().
                        filter(mapper -> mapper.supports(dtoClass))
                        .findFirst();
        if (matchingMapper.isEmpty()) {
            throw new IllegalArgumentException("No Mapper found for dtoClass: " + dtoClass);
        } else {
            return matchingMapper.get();
        }
    }
}
