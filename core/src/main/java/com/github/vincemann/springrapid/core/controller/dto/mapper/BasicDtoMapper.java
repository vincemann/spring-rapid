package com.github.vincemann.springrapid.core.controller.dto.mapper;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.util.IdPropertyNameUtils;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.modelmapper.*;
import org.modelmapper.internal.InheritingConfiguration;
import org.modelmapper.spi.NamingConvention;
import org.modelmapper.spi.PropertyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.capitalize;


/**
 * Maps a Dto to its ServiceEntity and vice versa, by using {@link ModelMapper}
 */
@Setter
@Getter
@Transactional
@NoArgsConstructor
@Order(Ordered.LOWEST_PRECEDENCE)
public class BasicDtoMapper implements DtoMapper<IdentifiableEntity<?>,Object> {

    private ModelMapper modelMapper;

    @Override
    public boolean supports(Class<?> dtoClass) {
        return true;
    }

    @Override
    public <T extends IdentifiableEntity<?>> T mapToEntity(Object source, Class<T> destinationClass) throws BadEntityException {
        try {
//            this.modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
//            this.modelMapper.getConfiguration().setPropertyCondition( ctx -> {
//                return !ctx.getDestinationType().getSimpleName().equals("Owner");
//            });
//            this.modelMapper.getConfiguration().setSkipNullEnabled(true);
            // todo will always create emtpy owner object as member of pet with id 0, instead of leaving owner field null....
            return this.modelMapper.map(source, destinationClass);
        }catch (MappingException e){
            throw new BadEntityException(e);
        }
    }

//    @Override
//    public <T> T mapToDto(IdentifiableEntity<?> source, Class<T> destinationClass) {
//        return modelMapper.map(source, destinationClass);
//    }

    @Override
    public <T> T mapToDto(IdentifiableEntity<?> sourceEntity, Class<T> destinationClass, String... fieldsToMap) {

        ModelMapper modelMapper = new ModelMapper();
        if (fieldsToMap.length > 0 ){
//            Set<String> propertiesToMap = Arrays.stream(fieldsToMap).map(IdPropertyNameUtils::transformIdFieldName).collect(Collectors.toSet());
            Set<String> propertiesToMap = Sets.newHashSet(fieldsToMap);
            propertiesToMap.add("id");
            List<String> alreadySeen = new ArrayList<>();
            NamingConvention namingConvention = new NamingConvention() {
                public boolean applies(String propertyName, PropertyType propertyType) {
                    if (propertyName.startsWith("set") || propertyName.startsWith("get")) {
//                        String property = IdPropertyNameUtils.transformIdFieldName(StringUtils.uncapitalize(propertyName.substring(3)));
                        String property = StringUtils.uncapitalize(propertyName.substring(3));
                        if (alreadySeen.contains(property)){
                            return false;
                        }
                        alreadySeen.add(property);
                        return propertiesToMap.contains(property);
                    }else {
                        return false;
                    }
                }
            };
            NamingConvention oldNamingConvention = modelMapper.getConfiguration().getDestinationNamingConvention();
//            modelMapper.getConfiguration().setSourceNamingConvention(namingConvention);
            modelMapper.getConfiguration().setDestinationNamingConvention(namingConvention);
            T mapped = modelMapper.map(sourceEntity, destinationClass);
            modelMapper.getConfiguration().setDestinationNamingConvention(oldNamingConvention);
            return mapped;
        }
        return modelMapper.map(sourceEntity, destinationClass);
    }

    @Autowired
    public void injectModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

}
