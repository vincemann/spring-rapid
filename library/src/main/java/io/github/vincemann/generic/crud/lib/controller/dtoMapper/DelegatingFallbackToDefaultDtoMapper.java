package io.github.vincemann.generic.crud.lib.controller.dtoMapper;

import io.github.vincemann.generic.crud.lib.config.WebComponent;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.exception.EntityMappingException;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@WebComponent
@Slf4j
public class DelegatingFallbackToDefaultDtoMapper implements DtoMapper {

    private List<DtoMapper> delegates;
    private DtoMapper defaultDelegate;

    @Autowired
    public DelegatingFallbackToDefaultDtoMapper(@Qualifier("default") DtoMapper defaultDelegate) {
        this.defaultDelegate = defaultDelegate;
    }

    @Override
    public boolean isDtoClassSupported(Class<? extends IdentifiableEntity> clazz) {
        try {
            findMapper(clazz);
            return true;
        }catch (IllegalArgumentException e){
            return false;
        }
    }

    @Autowired
    @Lazy
    public void setDelegates(List<DtoMapper> delegates) {
        this.delegates = delegates;
        this.delegates.remove(defaultDelegate);
        this.delegates.remove(this);
    }

    @Override
    public <T extends IdentifiableEntity> T mapToEntity(IdentifiableEntity dto, Class<T> destinationClass) throws EntityMappingException {
        return findMapper(dto.getClass()).mapToEntity(dto,destinationClass);
    }

    @Override
    public <T extends IdentifiableEntity> T mapToDto(IdentifiableEntity entity, Class<T> destinationClass) throws EntityMappingException {
        return findMapper(destinationClass).mapToDto(entity,destinationClass);
    }

    private DtoMapper findMapper(Class<? extends IdentifiableEntity> dtoClass) {
        List<DtoMapper> matchingMappers =
                delegates.stream().
                        filter(mapper -> mapper.isDtoClassSupported(dtoClass)).
                        collect(Collectors.toList());
        if(matchingMappers.isEmpty()){
            log.debug("No dtoMapper found in user specified mappers, returning default mapper: " + defaultDelegate);
            return defaultDelegate;
        }else if(matchingMappers.size()==1){
            log.debug("found dto mapper matching dto class: " + dtoClass + " is:" + matchingMappers.get(0));
            return matchingMappers.get(0);
        }else {
            throw new IllegalArgumentException("Found multiple matching mappers for: " + dtoClass);
        }
    }
}
