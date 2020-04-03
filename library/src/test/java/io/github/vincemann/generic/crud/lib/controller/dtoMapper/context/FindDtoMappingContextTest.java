package io.github.vincemann.generic.crud.lib.controller.dtoMapper.context;

import com.google.common.collect.Lists;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntityImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.CDATASection;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

class FindDtoMappingContextTest {

    DtoMappingContext context;
    DtoMappingInfo findInfo;
    DtoMappingInfo createInfo;
    DtoMappingInfo findAllInfo;
    List<String> roles;


    class PrivilegedFindDto extends IdentifiableEntityImpl<Long> {

    }

    class CreateDto extends IdentifiableEntityImpl<Long>{

    }

    class LessPrivilegedFindDto extends IdentifiableEntityImpl<Long>{

    }

    @BeforeEach
    void setUp() {
        roles = Lists.newArrayList("ROLE_USER", "ROLE_PEEK_DETAILED_USER_INFO");
        context = DtoMappingContextBuilder.builder()
                .withRoles(roles.toArray(new String[0]))
                .forEndpoint(CrudDtoEndpoint.FIND,Direction.RESPONSE, PrivilegedFindDto.class)
                .forEndpoint(CrudDtoEndpoint.FIND_ALL,Direction.RESPONSE,PrivilegedFindDto.class)
                .withoutRole()
                .forEndpoint(CrudDtoEndpoint.CREATE,CreateDto.class)
                .forEndpoint(CrudDtoEndpoint.FIND,Direction.RESPONSE,LessPrivilegedFindDto.class)
                .build();

        findInfo = DtoMappingInfo.builder()
                .direction(Direction.RESPONSE)
                .endpoint(CrudDtoEndpoint.FIND)
                .build();

        findAllInfo = DtoMappingInfo.builder()
                .endpoint(CrudDtoEndpoint.FIND_ALL)
                .direction(Direction.RESPONSE)
                .build();

        createInfo = DtoMappingInfo.builder()
                .direction(Direction.REQUEST)
                .endpoint(CrudDtoEndpoint.CREATE)
                .build();
    }

    @Test
    void findCreateEntryWithoutRoles_shouldFind() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        //when
        createInfo.setAuthorities(new ArrayList<>());
        Class<? extends IdentifiableEntity> foundClass = context.find(createInfo);
        //then
        Assertions.assertEquals(CreateDto.class,foundClass);
    }

    @Test
    void findCreateEntryWithRoles_shouldFindAndSilentIgnoreRoles() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        //when
        createInfo.setAuthorities(roles);
        Class<? extends IdentifiableEntity> foundClass = context.find(createInfo);
        //then
        Assertions.assertEquals(CreateDto.class,foundClass);
    }

    @Test
    void findFindEntryWithRoles_shouldFindPrivEntry() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        //when
        findInfo.setAuthorities(roles);
        Class<? extends IdentifiableEntity> foundClass = context.find(findInfo);
        //then
        Assertions.assertEquals(PrivilegedFindDto.class,foundClass);
    }

    @Test
    void findFindEntryWithoutRoles_shouldFindLessPrivEntry() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        findInfo.setAuthorities(new ArrayList<>());
        Class<? extends IdentifiableEntity> foundClass = context.find(findInfo);
        //then
        Assertions.assertEquals(LessPrivilegedFindDto.class,foundClass);
    }

    @Test
    void findOnlyAdminEntryWithoutRoles_shouldNotFind() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        findAllInfo.setAuthorities(new ArrayList<>());
        Assertions.assertThrows(IllegalArgumentException.class,()-> context.find(findAllInfo));
    }

    @Test
    void findOnlyAdminEntryWithAllPlusMoreRoles_shouldFind() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        ArrayList<String> moreRoles = Lists.newArrayList(roles);
        moreRoles.add("NEW_FANCY_ROLE");
        findAllInfo.setAuthorities(moreRoles);
        Class<? extends IdentifiableEntity> foundClass = context.find(findAllInfo);
        Assertions.assertEquals(PrivilegedFindDto.class,foundClass);
    }

    @Test
    void findFindEntryWithNotEnoughRoles_shouldFindLessPriv() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        ArrayList<String> lessRoles = Lists.newArrayList(roles);
        lessRoles.remove(0);
        findInfo.setAuthorities(lessRoles);
        Class<? extends IdentifiableEntity> foundClass = context.find(findInfo);
        Assertions.assertEquals(LessPrivilegedFindDto.class,foundClass);
    }

    @Test
    void findUnknownEntry_shouldNotFind(){
        DtoMappingInfo unknown = DtoMappingInfo.builder()
                .authorities(new ArrayList<>())
                .direction(Direction.REQUEST)
                .endpoint(CrudDtoEndpoint.FIND)
                .build();
        Assertions.assertThrows(IllegalArgumentException.class,()-> context.find(unknown));
    }

    @Test
    void findFindEntryWithWrongRoles_shouldFindLessPriv() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        ArrayList<String> wrongRoles = Lists.newArrayList(roles);
        wrongRoles.remove(roles.size()-1);
        wrongRoles.add("WRONG_ROLE");
        findInfo.setAuthorities(wrongRoles);
        Class<? extends IdentifiableEntity> foundClass = context.find(findInfo);
        Assertions.assertEquals(LessPrivilegedFindDto.class,foundClass);
    }


}