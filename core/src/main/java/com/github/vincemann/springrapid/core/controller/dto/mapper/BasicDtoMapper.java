package com.github.vincemann.springrapid.core.controller.dto.mapper;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.util.IdPropertyNameUtils;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.modelmapper.*;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.internal.InheritingConfiguration;
import org.modelmapper.spi.MatchingStrategy;
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
@Transactional
@NoArgsConstructor
@Order(Ordered.LOWEST_PRECEDENCE)
public class BasicDtoMapper implements DtoMapper<IdentifiableEntity<?>, Object> {

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

//            ModelMapper modelMapper = new ModelMapper();

            //todo use systemwide and strict matching strategy
            // , watch out for .setMatchingStrategy(MatchingStrategies.STRICT); maybe the InheritingConfigurations#equals method needs to check for that?
            ModelMapper modelMapper = createModelMapper(false);
            T mapped = modelMapper.map(source, destinationClass);
            cleanupModelMapper(modelMapper,false);
            return mapped;
//            return new ModelMapper().map(source, destinationClass);
        } catch (MappingException e) {
            throw new BadEntityException(e);
        }
    }

//    @Override
//    public <T> T mapToDto(IdentifiableEntity<?> source, Class<T> destinationClass) {
//        return modelMapper.map(source, destinationClass);
//    }

    @Override
    public <T> T mapToDto(IdentifiableEntity<?> sourceEntity, Class<T> destinationClass, String... fieldsToMap) {

//        ModelMapper modelMapper = new ModelMapper();
        ModelMapper modelMapper = createModelMapper(true);


//        MatchingStrategy matchingStrategy = modelMapper.getConfiguration().getMatchingStrategy();
        if (fieldsToMap.length == 0) {
//            System.err.println("mapping without properties defined -> map all");
//            NamingConvention destinationNamingConvention = modelMapper.getConfiguration().getDestinationNamingConvention();
//            System.err.println("dst Naming Convetion Hashcode: " + destinationNamingConvention.hashCode());
//            System.err.println("Matching strategy: " + matchingStrategy);
            return modelMapper.map(sourceEntity, destinationClass);
        }
        // properties to map defined:
//            Set<String> propertiesToMap = Arrays.stream(fieldsToMap).map(IdPropertyNameUtils::transformIdFieldName).collect(Collectors.toSet());
        Set<String> propertiesToMap = Sets.newHashSet(fieldsToMap);
        propertiesToMap.add("id");

//        System.err.println("Matching strategy: " + matchingStrategy);

//        System.err.println("properties to map: (except id) " + propertiesToMap);

        List<String> alreadySeen = new ArrayList<>();
        NamingConvention namingConvention = new NamingConvention() {
            public boolean applies(String propertyName, PropertyType propertyType) {
                if (propertyName.startsWith("set") || propertyName.startsWith("get")) {
//                    System.err.println("mapping property: " + propertyName);
//                        String property = IdPropertyNameUtils.transformIdFieldName(StringUtils.uncapitalize(propertyName.substring(3)));
                    String property = StringUtils.uncapitalize(propertyName.substring(3));
                    if (alreadySeen.contains(property)) {
//                        System.err.println("already seen, skip");
                        return false;
                    }
                    alreadySeen.add(property);
                    boolean contains = propertiesToMap.contains(property);
//                    System.err.println("mapping property: " + contains);
                    return contains;
                } else {
                    return false;
                }
            }
        };
//        NamingConvention oldNamingConvention = modelMapper.getConfiguration().getDestinationNamingConvention();
//        System.err.println("Old naming convention: " + oldNamingConvention);
//        System.err.println("Using naming convention: " + namingConvention.hashCode());
//            modelMapper.getConfiguration().setSourceNamingConvention(namingConvention);
        modelMapper.getConfiguration().setDestinationNamingConvention(namingConvention);
        T mapped = modelMapper.map(sourceEntity, destinationClass);
        cleanupModelMapper(modelMapper,true);
//        modelMapper.getConfiguration().setDestinationNamingConvention(oldNamingConvention);
        return mapped;
    }

    /**
     * Can be overwritten in order to configure modelmapper
     * @return
     */
    protected ModelMapper createModelMapper(boolean toDto){
        if (this.modelMapper == null)
            return new ModelMapper();
        else
            return this.modelMapper;
    }

    protected ModelMapper getModelMapper() {
        return modelMapper;
    }

    /**
     * Can be used to define one class wide mapper, otherwise a new modelmapper will get created on
     * every mapping call.
     * Watch out for configuration in {@link this#mapToDto(IdentifiableEntity, Class, String...)}
     * @see this#getModelMapper()
     * @param modelMapper
     */
    public void createPermanentModelMapper(ModelMapper modelMapper){
        this.modelMapper=modelMapper;
    }

    /**
     * Makes sense in conjunction with {@link this#createPermanentModelMapper(ModelMapper)}
     */
    protected void cleanupModelMapper(ModelMapper modelMapper, boolean toDto){

    }




//    @Autowired
//    public void injectModelMapper(ModelMapper modelMapper) {
//        this.modelMapper = modelMapper;
//        this.modelMapper.getConfiguration()
//                .setMatchingStrategy(MatchingStrategies.STRICT);
//    }

}
