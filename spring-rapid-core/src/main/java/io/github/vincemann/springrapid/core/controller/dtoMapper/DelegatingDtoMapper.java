package io.github.vincemann.springrapid.core.controller.dtoMapper;

import io.github.vincemann.springrapid.core.advice.log.LogInteraction;
import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.core.service.exception.BadEntityException;
import io.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional
public class DelegatingDtoMapper implements DtoMapper<IdentifiableEntity<?>, Object> {

    private List<DtoMapper<?, ?>> delegates = new ArrayList<>();

    @Override
    public boolean supports(Class<?> dtoClass) {
        DtoMapper<?,?> mapper = findMapper(dtoClass);
        return mapper != null;

    }


    @LogInteraction
    @Override
    public <T extends IdentifiableEntity<?>> T mapToEntity(Object dto, Class<T> destinationClass) throws EntityNotFoundException, BadEntityException {
        return (T) findMapper(dto.getClass())
                .mapToEntity(dto, destinationClass);
    }

    public void registerDelegate(DtoMapper delegate) {
        this.delegates.add(delegate);
    }

    @Override
    public <T> T mapToDto(IdentifiableEntity<?> source, Class<T> destinationClass) {
        return (T) findMapper(destinationClass)
                .mapToDto(source, destinationClass);
    }


    //    @LogInteraction
//    @Override
//    public <T> T mapToDto(IdentifiableEntity<?> entity, Class<T> destinationClass)  {
//        return findMapper(destinationClass)
//                .mapToDto(entity,destinationClass);
//    }

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
