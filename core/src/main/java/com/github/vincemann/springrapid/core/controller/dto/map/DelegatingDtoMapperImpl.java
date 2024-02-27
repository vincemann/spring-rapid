package com.github.vincemann.springrapid.core.controller.dto.map;


import com.github.vincemann.springrapid.core.controller.dto.DtoPostProcessor;
import com.github.vincemann.springrapid.core.controller.dto.EntityPostProcessor;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.IdPropertyNameUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.*;

@Slf4j
public class DelegatingDtoMapperImpl implements DelegatingDtoMapper{

    private List<DtoMapper<?, ?>> delegates = new ArrayList<>();
    private List<EntityPostProcessor> entityPostProcessors = new ArrayList<>();
    private List<DtoPostProcessor> dtoPostProcessors = new ArrayList<>();
    private EntityManager entityManager;


    @Transactional
    @Override
    public <T extends IdentifiableEntity<?>> T mapToEntity(Object dto, Class<T> destinationClass) throws EntityNotFoundException, BadEntityException {
        T mapped = (T) findMapper(dto.getClass())
                .mapToEntity(dto, destinationClass);
        for (EntityPostProcessor pp : entityPostProcessors) {
            if (pp.supports(mapped.getClass(), dto.getClass())) {
                pp.postProcessEntity(mapped, dto);
            }
        }
        return mapped;
    }


    public void register(DtoMapper delegate) {
        this.delegates.add(delegate);
    }

    @Override
    public void registerEntityPostProcessor(EntityPostProcessor postProcessor){
        this.entityPostProcessors.add(postProcessor);
    }

    @Override
    public void registerDtoPostProcessor(DtoPostProcessor postProcessor){
        this.dtoPostProcessors.add(postProcessor);
    }

    @Transactional(readOnly = true)
    @Override
    public <T> Set<T> mapToDto(Set<? extends IdentifiableEntity<?>> source, Class<T> destinationClass, String... fieldsToMap) throws BadEntityException {
        entityManager.merge(source);
        Set<T> result = new HashSet<>();
        for (IdentifiableEntity<?> entity : source) {
            result.add(mapToDto(entity,destinationClass,fieldsToMap));
        }
        return result;
    }

    @Transactional(readOnly = true)
    public <T> List<T> mapToDto(List<? extends IdentifiableEntity<?>> source, Class<T> destinationClass, String... fieldsToMap) throws BadEntityException {
        entityManager.merge(source);
        List<T> result = new ArrayList<>();
        for (IdentifiableEntity<?> entity : source) {
            result.add(mapToDto(entity,destinationClass,fieldsToMap));
        }
        return result;
    }

    //@LogInteraction
    @Transactional(readOnly = true)
    public <T> T mapToDto(IdentifiableEntity<?> source, Class<T> destinationClass,String... fieldsToMap) throws BadEntityException {
        // source entity is detached and might not have all collections lazy loaded for this dto mapping -> merge
        entityManager.merge(source);
        T dto = (T) findMapper(destinationClass)
                .mapToDto(source, destinationClass,fieldsToMap);
        for (DtoPostProcessor pp : dtoPostProcessors) {
            if (pp.supports(source.getClass(), dto.getClass())) {
                pp.postProcessDto(dto, source, IdPropertyNameUtils.transformIdFieldNames(fieldsToMap));
            }
        }
        return dto;
    }

    protected DtoMapper findMapper(Class<?> dtoClass) {
        Optional<DtoMapper<?, ?>> matchingMapper =
                delegates.stream()
                        .filter(mapper -> mapper.supports(dtoClass))
                        .findFirst();
        if (matchingMapper.isEmpty()) {
            throw new IllegalArgumentException("No Mapper found for dtoClass: " + dtoClass);
        } else {
            return matchingMapper.get();
        }
    }

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

}
