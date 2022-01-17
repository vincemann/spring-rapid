package com.github.vincemann.springrapid.core.controller.dto.mapper;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.util.IdPropertyNameUtils;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.TypeMap;
import org.modelmapper.internal.InheritingConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

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
            return modelMapper.map(source, destinationClass);
        }catch (MappingException e){
            throw new BadEntityException(e);
        }
    }

//    @Override
//    public <T> T mapToDto(IdentifiableEntity<?> source, Class<T> destinationClass) {
//        return modelMapper.map(source, destinationClass);
//    }

    @Override
    public <T> T mapToDto(IdentifiableEntity<?> source, Class<T> destinationClass, String... fieldsToMap) {
        // todo maybe i should use the injected modelmapper, but i dont know how to remove the PropertyMap after mapping, which would introduce state
        // maybe i should always create new modelmapper instance when mapping
        // OR i call getter of PropertyMaps via reflections and remove like that

//        ModelMapper modelMapper = new ModelMapper();

        // todo maybe add cache with source,dstclass,fieldsToMap as key and TypeMap as value
        TypeMap typeMap = this.modelMapper.createTypeMap(source, destinationClass);

        if (fieldsToMap.length > 0 ) {
            Set<String> propertiesToSkip = com.github.vincemann.springrapid.core.util.ReflectionUtils.findAllFieldNamesExcept(Sets.newHashSet(fieldsToMap), destinationClass);
            // https://amydegregorio.com/2018/05/23/skipping-fields-with-modelmapper/
            PropertyMap<Object, Object> skippingPropertiesMap = new PropertyMap<Object, Object>() {
                @Override
                protected void configure() {
                    for (String property : propertiesToSkip) {
                        // dto properties, so map to entity property names with IdPropertyNameUtils
                        String entityPropertyName = IdPropertyNameUtils.transformIdFieldName(property);
                        Method setter = ReflectionUtils.findMethod(source.getClass(),
                                "set" + capitalize(entityPropertyName)
                        );
                        try {
                            if (setter == null) {
                                throw new RuntimeException("No setter found for entity property: " + entityPropertyName);
                            }
                            setter.invoke(skip(), new Object[]{null});
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeException("No setter found for entity property: " + entityPropertyName);
                        }
                    }
                }
            };
            typeMap.addMappings(skippingPropertiesMap);
        }
//        ((InheritingConfiguration) modelMapper.getConfiguration()).typeMapStore.
        return modelMapper.map(source, destinationClass);

    }

    @Autowired
    public void injectModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

}
