package com.github.vincemann.springrapid.coredemo.mapper;

import com.github.vincemann.springrapid.coredemo.model.abs.Person;
import com.google.common.collect.Sets;
import lombok.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.*;
import org.modelmapper.spi.*;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Date;
import java.util.Set;

import static org.springframework.util.StringUtils.capitalize;


public class ModelMapperExerciseTests {

    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    @Getter
    @Setter
    private static class Gil {
        private String name;
        private Long age;
        private Long born;
        private String birthtown;
    }

    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    @Getter
    @Setter
    private static class GilDto {
        private String name;
        private String surname;
        private String birthtown;
        private String age;
        private Date born;
    }

    ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        this.modelMapper = new ModelMapper();
    }

    @Test
    public void testSimpleMapping() {
        Gil gil = Gil.builder()
                .age(25L)
                .name("dessengil")
                .build();

        GilDto mapped = modelMapper.map(gil, GilDto.class);
        Assertions.assertEquals(gil.name, mapped.name);
        Assertions.assertEquals(gil.age.toString(), mapped.age);
    }

    @Test
    public void testExplicitMapping() {
        Gil gil = Gil.builder()
                .age(25L)
                .name("dessengil")
                .build();

        TypeMap<Gil, GilDto> typeMap = modelMapper.createTypeMap(Gil.class, GilDto.class);
        // we only supply v's type
        typeMap.<String>addMapping(source -> source.getName(), (dst, v) -> {
            dst.setSurname(v + "!");
        });

        GilDto mapped = modelMapper.map(gil, GilDto.class);
        Assertions.assertEquals(gil.name, mapped.name);
        Assertions.assertEquals(gil.name + "!", mapped.surname + "!");
        Assertions.assertEquals(gil.age.toString(), mapped.age);
    }

    @Test
    public void testExplicitTypeConversionMapping() {
        long now = new Date().getTime();
        long offset = 20000;

        Gil gil = Gil.builder()
                .age(25L)
                .born(now)
                .name("dessengil")
                .build();


        TypeMap<Gil, GilDto> typeMap = modelMapper.createTypeMap(Gil.class, GilDto.class);
        // we only supply v's type

        Converter<Long,Date> dateOffsetConverter = new Converter<Long, Date>() {
            @Override
            public Date convert(MappingContext<Long, Date> context) {
                return Date.from(Instant.ofEpochMilli(context.getSource() + offset));
            }
        };
        typeMap.addMappings(mapper -> mapper.using(dateOffsetConverter).map(Gil::getBorn,GilDto::setBorn));

        GilDto mapped = modelMapper.map(gil, GilDto.class);
        Assertions.assertEquals(gil.name, mapped.name);
//        Assertions.assertEquals(new Date(now+offset), mapped.born);
        Assertions.assertEquals(Date.from(Instant.ofEpochMilli(now + offset)), mapped.born);
        Assertions.assertEquals(gil.age.toString(), mapped.age);
    }

    @Test
    public void testSkipSingleProperty() {

        Gil gil = Gil.builder()
                .age(25L)
                .name("dessengil")
                .build();


        TypeMap<Gil, GilDto> typeMap = modelMapper.createTypeMap(Gil.class, GilDto.class);
        // we only supply v's type
        typeMap.addMappings(mapper -> mapper.skip(GilDto::setAge));

        GilDto mapped = modelMapper.map(gil, GilDto.class);
        Assertions.assertEquals(gil.name, mapped.name);
        Assertions.assertEquals(null, mapped.age);
    }

    @Test
    public void testSkipSinglePropertyViaConfigure() {

        Gil gil = Gil.builder()
                .age(25L)
                .name("dessengil")
                .build();


        TypeMap<Gil, GilDto> typeMap = modelMapper.createTypeMap(Gil.class, GilDto.class);

       String propertyToSkip = "age";

        PropertyMap<Gil, GilDto> skippingPropertyMap = new PropertyMap<>() {
            @Override
            protected void configure() {
                skip().setAge(null);
            }
        };

        modelMapper.addMappings(skippingPropertyMap);
        GilDto mapped = modelMapper.map(gil, GilDto.class);
        Assertions.assertEquals(gil.name, mapped.name);
        Assertions.assertEquals(null, mapped.age);
    }


    @Test
    public void testLimitPropertiesToMapByName(){

        Gil gil = Gil.builder()
                .age(25L)
                .name("dessengil")
                .build();


        // https://groups.google.com/g/modelmapper/c/5sJdaMtEydg
        String propertyToMap = "name";

// Naming convention that matches fields and properties whose names begin with "with"
        NamingConvention yourConvention = new NamingConvention() {
            public boolean applies(String propertyName, PropertyType propertyType) {
                String property = propertyName.toLowerCase().substring(3);
                return property.equals(propertyToMap);
            }
        };


//        modelMapper.getConfiguration().setDestinationNamingConvention(yourConvention);
        modelMapper.getConfiguration().setSourceNamingConvention(yourConvention);
        GilDto mapped = modelMapper.map(gil, GilDto.class);
        Assertions.assertEquals(gil.name, mapped.name);
        Assertions.assertEquals(null, mapped.age);
    }

    @Test
    public void testOnlyMapPropertiesFromPropertyNameList() {

        Gil gil = Gil.builder()
                .age(25L)
                .name("dessengil")
                .birthtown("huston")
                .build();

        Set<String> propertiesToMap = Sets.newHashSet( "age","birthtown");
        NamingConvention yourConvention = new NamingConvention() {
            public boolean applies(String propertyName, PropertyType propertyType) {
                String property = propertyName.toLowerCase().substring(3);
                return propertiesToMap.contains(property);
            }
        };


//        modelMapper.getConfiguration().setDestinationNamingConvention(yourConvention);
        modelMapper.getConfiguration().setSourceNamingConvention(yourConvention);
        GilDto mapped = modelMapper.map(gil, GilDto.class);
        Assertions.assertEquals(gil.age.toString(), mapped.age);
        Assertions.assertEquals(gil.birthtown, mapped.birthtown);
        Assertions.assertNull(mapped.name);
        Assertions.assertNull(mapped.born);

    }

    @Test
    public void testOnlyMapPropertiesFromPropertyNameList_byUsingCondition() {

        Gil gil = Gil.builder()
                .age(25L)
                .name("dessengil")
                .build();

        Set<String> propertiesToMap = Sets.newHashSet( "age");

        Condition onlyMapCertainProperties = ctx -> {
            String typeName = ctx.getGenericDestinationType().getTypeName();
            return propertiesToMap.contains(typeName);
        };

        TypeMap<Gil, GilDto> typeMap = modelMapper.createTypeMap(Gil.class, GilDto.class);
        typeMap.addMappings(mapper -> mapper.when(onlyMapCertainProperties));


        GilDto mapped = modelMapper.map(gil, GilDto.class);
        Assertions.assertEquals(gil.age.toString(), mapped.age);
        Assertions.assertNull(mapped.birthtown);
        Assertions.assertNull(mapped.name);
        Assertions.assertNull(mapped.born);

    }


}