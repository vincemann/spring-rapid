package io.github.vincemann.generic.crud.lib.controller.dtoMapper;

import io.github.vincemann.generic.crud.lib.controller.dtoMapper.exception.DtoMappingException;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class DelegatingFallbackToDefaultDtoMapper implements DtoMapper {

    private List<DtoMapper> delegates = new ArrayList<>();
    private DtoMapper defaultDelegate;

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

    public void registerDelegate(DtoMapper delegate) {
        this.delegates.add(delegate);
    }

    @Override
    public <T extends IdentifiableEntity> T mapToEntity(IdentifiableEntity dto, Class<T> destinationClass) throws DtoMappingException {
        log.info("_____________________________________________________________________________");
        log.info("Map from Dto to Entity-Class: "+destinationClass+" inputDto: " + dto);
        log.info("_____________________________________________________________________________");
        return findMapper(dto.getClass())
                .mapToEntity(dto,destinationClass);
    }

    @Override
    public <T extends IdentifiableEntity> T mapToDto(IdentifiableEntity entity, Class<T> destinationClass) throws DtoMappingException {
        log.info("_____________________________________________________________________________");
        log.info("Map from Entity to Dto-Class: "+destinationClass+" inputEntity: " + entity);
        log.info("_____________________________________________________________________________");
        return findMapper(destinationClass)
                .mapToDto(entity,destinationClass);
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
