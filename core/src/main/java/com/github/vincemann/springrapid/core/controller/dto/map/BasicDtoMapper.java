package com.github.vincemann.springrapid.core.controller.dto.map;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.google.common.collect.Sets;
import lombok.NoArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.modelmapper.*;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.NamingConvention;
import org.modelmapper.spi.PropertyType;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.log.LogMessage;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * Maps a Dto to its ServiceEntity and vice versa, by using {@link ModelMapper}
 */
@Transactional
@NoArgsConstructor
@Order(Ordered.LOWEST_PRECEDENCE)
public class BasicDtoMapper implements DtoMapper<IdentifiableEntity<?>, Object> {

    @Override
    public boolean supports(Class<?> dtoClass) {
        return true;
    }

    @Override
    public <T extends IdentifiableEntity<?>> T mapToEntity(Object source, Class<T> destinationClass) throws BadEntityException {
        try {
            ModelMapper modelMapper = createModelMapper(false);
            return modelMapper.map(source, destinationClass);
        } catch (MappingException e) {
            throw new BadEntityException(e);
        }
    }

    @Override
    public <T> T mapToDto(IdentifiableEntity<?> sourceEntity, Class<T> destinationClass, String... fieldsToMap) {

        ModelMapper modelMapper = createModelMapper(true);
        if (fieldsToMap.length == 0) {
            return modelMapper.map(sourceEntity, destinationClass);
        }
        Set<String> propertiesToMap = Sets.newHashSet(fieldsToMap);
        propertiesToMap.add("id");

        List<String> alreadySeen = new ArrayList<>();
        NamingConvention namingConvention = new NamingConvention() {
            public boolean applies(String propertyName, PropertyType propertyType) {
                if (propertyName.startsWith("set") || propertyName.startsWith("get")) {
                    String property = StringUtils.uncapitalize(propertyName.substring(3));
                    if (alreadySeen.contains(property)) {
                        return false;
                    }
                    alreadySeen.add(property);
                    return propertiesToMap.contains(property);
                } else {
                    return false;
                }
            }
        };
        modelMapper.getConfiguration().setDestinationNamingConvention(namingConvention);
        return modelMapper.map(sourceEntity, destinationClass);
    }

    /**
     * Can be overwritten in order to configure modelmapper
     *
     * @return
     */
    protected ModelMapper createModelMapper(boolean toDto) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return mapper;
    }

}
