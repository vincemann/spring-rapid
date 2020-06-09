package com.github.vincemann.springrapid.core.controller.dtoMapper.context;

import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import com.github.vincemann.springrapid.commons.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

class RapidDtoMappingContextTest {

    RapidDtoMappingContext context;
    DtoMappingInfo findInfo;
    DtoMappingInfo createInfo;
    DtoMappingInfo findAllInfo;
    List<String> roles;
    String userRole = "ROLE_USER";
    String adminRole = "ROLE_ADMIN";
    String peekRole = "ROLE_PEEK_DETAILED_USER_INFO";
    DtoMappingInfo updateInfo;


    class PrivilegedFindDto extends IdentifiableEntityImpl<Long> {

    }

    class CreateDto extends IdentifiableEntityImpl<Long>{

    }

    class AdminUpdateForeignUserDto extends IdentifiableEntityImpl<Long>{

    }

    class AdminUpdateOwnDto extends IdentifiableEntityImpl<Long>{

    }

    class LessPrivilegedFindDto extends IdentifiableEntityImpl<Long>{

    }

    @BeforeEach
    void setUp() {
        roles = Lists.newArrayList(userRole,peekRole);
        context = DtoMappingContextBuilder.builder()
                .withRoles(roles.toArray(new String[0]))
                .forEndpoint(RapidDtoEndpoint.FIND,Direction.RESPONSE, PrivilegedFindDto.class)
                .forEndpoint(RapidDtoEndpoint.FIND_ALL,Direction.RESPONSE,PrivilegedFindDto.class)
                .withRoles(adminRole)
                .withPrincipal(DtoMappingInfo.Principal.FOREIGN)
                .forEndpoint(RapidDtoEndpoint.UPDATE,Direction.REQUEST,AdminUpdateForeignUserDto.class)
                .withPrincipal(DtoMappingInfo.Principal.OWN)
                .forEndpoint(RapidDtoEndpoint.UPDATE,Direction.REQUEST,AdminUpdateOwnDto.class)
                .withAllPrincipals()
                .withAllRoles()
                .forEndpoint(RapidDtoEndpoint.CREATE,CreateDto.class)
                .forEndpoint(RapidDtoEndpoint.FIND,Direction.RESPONSE,LessPrivilegedFindDto.class)
                .build();

        findInfo = DtoMappingInfo.builder()
                .direction(Direction.RESPONSE)
                .endpoint(RapidDtoEndpoint.FIND)
                .build();

        findAllInfo = DtoMappingInfo.builder()
                .endpoint(RapidDtoEndpoint.FIND_ALL)
                .direction(Direction.RESPONSE)
                .build();

        createInfo = DtoMappingInfo.builder()
                .direction(Direction.REQUEST)
                .endpoint(RapidDtoEndpoint.CREATE)
                .build();

        updateInfo = DtoMappingInfo.builder()
                .direction(Direction.REQUEST)
                .endpoint(RapidDtoEndpoint.UPDATE)
                .build();
    }

    @Test
    void findCreateEntryWithoutRoles_shouldFind() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        //when
        createInfo.setAuthorities(new ArrayList<>());
        Class<?> foundClass = context.find(createInfo);
        //then
        Assertions.assertEquals(CreateDto.class,foundClass);
    }

    @Test
    void findCreateEntryWithRoles_shouldFindAndSilentIgnoreRoles() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        //when
        createInfo.setAuthorities(roles);
        Class<?> foundClass = context.find(createInfo);
        //then
        Assertions.assertEquals(CreateDto.class,foundClass);
    }

    @Test
    void findFindEntryWithRoles_shouldFindPrivEntry() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        //when
        findInfo.setAuthorities(roles);
        Class<?> foundClass = context.find(findInfo);
        //then
        Assertions.assertEquals(PrivilegedFindDto.class,foundClass);
    }

    @Test
    void findFindEntryWithoutRoles_shouldFindLessPrivEntry() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        findInfo.setAuthorities(new ArrayList<>());
        Class<?> foundClass = context.find(findInfo);
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
        Class<?> foundClass = context.find(findAllInfo);
        Assertions.assertEquals(PrivilegedFindDto.class,foundClass);
    }

    @Test
    void findFindEntryWithNotEnoughRoles_shouldFindLessPriv() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        ArrayList<String> lessRoles = Lists.newArrayList(roles);
        lessRoles.remove(0);
        findInfo.setAuthorities(lessRoles);
        Class<?> foundClass = context.find(findInfo);
        Assertions.assertEquals(LessPrivilegedFindDto.class,foundClass);
    }

    @Test
    void findUnknownEntry_shouldNotFind(){
        DtoMappingInfo unknown = DtoMappingInfo.builder()
                .authorities(new ArrayList<>())
                .direction(Direction.REQUEST)
                .endpoint(RapidDtoEndpoint.FIND)
                .build();
        Assertions.assertThrows(IllegalArgumentException.class,()-> context.find(unknown));
    }

    @Test
    void findFindEntryWithWrongRoles_shouldFindLessPriv() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        ArrayList<String> wrongRoles = Lists.newArrayList(roles);
        wrongRoles.remove(roles.size()-1);
        wrongRoles.add("WRONG_ROLE");
        findInfo.setAuthorities(wrongRoles);
        Class<?> foundClass = context.find(findInfo);
        Assertions.assertEquals(LessPrivilegedFindDto.class,foundClass);
    }

    @Test
    public void adminUpdatesOwn(){
        updateInfo.setAuthorities(Lists.newArrayList(adminRole));
        updateInfo.setPrincipal(DtoMappingInfo.Principal.OWN);
        Class<?> foundClass = context.find(updateInfo);
        Assertions.assertEquals(AdminUpdateOwnDto.class,foundClass);
    }

    @Test
    public void adminUpdatesForeign(){
        updateInfo.setAuthorities(Lists.newArrayList(adminRole));
        updateInfo.setPrincipal(DtoMappingInfo.Principal.FOREIGN);
        Class<?> foundClass = context.find(updateInfo);
        Assertions.assertEquals(AdminUpdateForeignUserDto.class,foundClass);
    }

}