package io.github.vincemann.generic.crud.lib.controller.dtoMapper.finder;

import io.github.vincemann.generic.crud.lib.controller.dtoMapper.DtoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class FallbackToDefault_DtoMapperFinder implements DtoMapperFinder {

    private List<DtoMapper> dtoMappers;
    private DtoMapper defaultMapper;

    @Autowired
    public FallbackToDefault_DtoMapperFinder(List<DtoMapper> dtoMappers, @Qualifier("default") DtoMapper defaultMapper) {
        this.dtoMappers = dtoMappers;
        this.defaultMapper = defaultMapper;
    }

    @Override
    public DtoMapper find(Class<?> dtoClass) {
        List<DtoMapper> matchingMappers =
                dtoMappers.stream().
                        filter(mapper -> dtoClass.equals(mapper.getDtoClass())).
                        collect(Collectors.toList());
        if(matchingMappers.isEmpty()){
            log.debug("No dtoMapper found in user specified mappers, returning default mapper: " +defaultMapper);
            return defaultMapper;
        }else if(matchingMappers.size()==1){
            log.debug("found dto mapper matching dto class: " + dtoClass + " is:" + matchingMappers.get(0));
            return matchingMappers.get(0);
        }else {
            throw new IllegalArgumentException("Found multiple matching mappers for: " + dtoClass);
        }
    }
}
